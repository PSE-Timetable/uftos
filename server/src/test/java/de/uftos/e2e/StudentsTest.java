package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateIdListJson;
import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateStudentGroupJson;
import static de.uftos.utils.JsonGenerator.generateStudentJson;
import static de.uftos.utils.JsonGenerator.generateTagJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StudentsTest {

  private static final String FIRST_STUDENT_FIRST_NAME = "Karen";
  private static final String FIRST_STUDENT_LAST_NAME = "Mustermann";
  private static final String SECOND_STUDENT_FIRST_NAME = "Jason";
  private static final String SECOND_STUDENT_LAST_NAME = "Mustermann";
  private static final String GROUP_NAME = "Ethics";
  private static final String TAG_NAME = "Visual Impairment";

  static String firstStudent;
  static String secondStudent;
  static String tagId;
  static String groupId;

  @BeforeAll
  static void createTestStudents() throws JSONException {
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

    firstStudent = given().contentType(ContentType.JSON)
        .body(generateStudentJson(FIRST_STUDENT_FIRST_NAME, FIRST_STUDENT_LAST_NAME, List.of(),
            List.of()))
        .when()
        .post("/students")
        .then()
        .log().ifValidationFails(LogDetail.ALL)
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo(FIRST_STUDENT_FIRST_NAME))
        .body("lastName", equalTo(FIRST_STUDENT_LAST_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    secondStudent = given().contentType(ContentType.JSON)
        .body(generateStudentJson(SECOND_STUDENT_FIRST_NAME, SECOND_STUDENT_LAST_NAME, List.of(),
            List.of(tagId)))
        .when()
        .post("/students")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo(SECOND_STUDENT_FIRST_NAME))
        .body("lastName", equalTo(SECOND_STUDENT_LAST_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    groupId = given().contentType(ContentType.JSON)
        .body(generateStudentGroupJson(GROUP_NAME, List.of(secondStudent), List.of(),
            List.of(), List.of()))
        .when()
        .post("/student-groups")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(GROUP_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");
  }

  @AfterAll
  static void deleteCreatedStudents() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/student-groups/{id}", groupId)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(firstStudent, secondStudent))
        .delete("/students")
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
  void getAllStudents() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getStudentsWithTag() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("tags", List.of(tagId))
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondStudent))
        .log().ifValidationFails();
  }

  @Test
  void getStudentsWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("search", FIRST_STUDENT_FIRST_NAME)
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(firstStudent))
        .log().ifValidationFails();

  }

  @Test
  void getStudentsWithStudentGroup() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("groups", List.of(groupId))
        .when()
        .get("/students")
        .then()
        .log().ifValidationFails(LogDetail.ALL)
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondStudent))
        .log().ifValidationFails();
  }
}
