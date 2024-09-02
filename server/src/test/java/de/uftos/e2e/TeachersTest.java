package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateIdListJson;
import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateSubjectJson;
import static de.uftos.utils.JsonGenerator.generateTagJson;
import static de.uftos.utils.JsonGenerator.generateTeacherJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TeachersTest {

  private static final String FIRST_TEACHER_FIRST_NAME = "Karen";
  private static final String FIRST_TEACHER_LAST_NAME = "Mustermann";
  private static final String FIRST_TEACHER_ACRONYM = "KM";
  private static final String SECOND_TEACHER_FIRST_NAME = "Jason";
  private static final String SECOND_TEACHER_LAST_NAME = "Mustermann";
  private static final String SECOND_TEACHER_ACRONYM = "JM";
  private static final String TAG_NAME = "Sign Language";
  private static final String SUBJECT_NAME = "Maths";

  static String firstTeacher;
  static String secondTeacher;
  static String tagId;
  static String subjectId;

  @BeforeAll
  static void createTestTeachers() throws JSONException {
    tagId = given().contentType(ContentType.JSON)
        .body(generateTagJson(TAG_NAME))
        .when()
        .post("/tags")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(TAG_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    subjectId = given().contentType(ContentType.JSON)
        .body(generateSubjectJson(SUBJECT_NAME, "ffffff"))
        .when()
        .post("/subjects")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SUBJECT_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");


    firstTeacher = given().contentType(ContentType.JSON)
        .body(generateTeacherJson(FIRST_TEACHER_FIRST_NAME, FIRST_TEACHER_LAST_NAME,
            FIRST_TEACHER_ACRONYM, Collections.emptyList(), Collections.emptyList()))
        .when()
        .post("/teachers")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo(FIRST_TEACHER_FIRST_NAME))
        .body("lastName", equalTo(FIRST_TEACHER_LAST_NAME))
        .body("acronym", equalTo(FIRST_TEACHER_ACRONYM))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    secondTeacher = given().contentType(ContentType.JSON)
        .body(generateTeacherJson(SECOND_TEACHER_FIRST_NAME, SECOND_TEACHER_LAST_NAME,
            SECOND_TEACHER_ACRONYM, List.of(subjectId), List.of(tagId)))
        .when()
        .post("/teachers")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo(SECOND_TEACHER_FIRST_NAME))
        .body("lastName", equalTo(SECOND_TEACHER_LAST_NAME))
        .body("acronym", equalTo(SECOND_TEACHER_ACRONYM))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");
  }

  @AfterAll
  static void deleteCreatedEntities() {
    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(firstTeacher, secondTeacher))
        .delete("/teachers")
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(subjectId))
        .delete("/subjects")
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(tagId))
        .delete("/tags")
        .then()
        .statusCode(200);
  }

  @Test
  void getAllTeachers() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/teachers")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getTeachersWithTag() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("tags", List.of(tagId))
        .when()
        .get("/teachers")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondTeacher))
        .log().ifValidationFails();
  }

  @Test
  void getTeachersWithSubject() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("subjects", List.of(subjectId))
        .when()
        .get("/teachers")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondTeacher))
        .log().ifValidationFails();
  }

  @Test
  void getTeachersWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("search", FIRST_TEACHER_FIRST_NAME)
        .when()
        .get("/teachers")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(firstTeacher))
        .log().ifValidationFails();

  }
}
