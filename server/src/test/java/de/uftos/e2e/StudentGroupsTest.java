package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateGradeJson;
import static de.uftos.utils.JsonGenerator.generateIdListJson;
import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateStudentGroupJson;
import static de.uftos.utils.JsonGenerator.generateStudentJson;
import static de.uftos.utils.JsonGenerator.generateSubjectJson;
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

@SuppressWarnings("checkstyle:MissingJavadocType")
public class StudentGroupsTest {
  private static final String FIRST_STUDENT_GROUP_NAME = "5-Ethics";
  private static final String SECOND_STUDENT_GROUP_NAME = "5-Religion";
  private static final String STUDENT_FIRST_NAME = "Max";
  private static final String STUDENT_LAST_NAME = "Mustermann";
  private static final String GRADE_NAME = "5";
  private static final String SUBJECT_NAME = "Maths";
  private static final String TAG_NAME = "Visual Impairment";

  static String firstStudentGroup;
  static String secondStudentGroup;
  static String studentId;
  static String gradeId;
  static String tagId;
  static String subjectId;


  @BeforeAll
  static void createTestStudentGroups() throws JSONException {
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

    studentId = given().contentType(ContentType.JSON)
        .body(generateStudentJson(STUDENT_FIRST_NAME, STUDENT_LAST_NAME, Collections.emptyList(),
            Collections.emptyList()))
        .when()
        .post("/students")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo(STUDENT_FIRST_NAME))
        .body("lastName", equalTo(STUDENT_LAST_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");


    gradeId = given().contentType(ContentType.JSON)
        .body(generateGradeJson(GRADE_NAME, null, Collections.emptyList(), Collections.emptyList()))
        .when()
        .post("/grades")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(GRADE_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    subjectId = given().contentType(ContentType.JSON)
        .body(generateSubjectJson(SUBJECT_NAME))
        .when()
        .post("/subjects")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SUBJECT_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");


    firstStudentGroup = given().contentType(ContentType.JSON)
        .body(generateStudentGroupJson(FIRST_STUDENT_GROUP_NAME, Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
        .when()
        .post("/student-groups")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(FIRST_STUDENT_GROUP_NAME))
        .log().all()
        .extract()
        .body().jsonPath().getString("id");

    secondStudentGroup = given().contentType(ContentType.JSON)
        .body(generateStudentGroupJson(SECOND_STUDENT_GROUP_NAME, List.of(studentId),
            List.of(gradeId), List.of(tagId), List.of(subjectId)))
        .when()
        .post("/student-groups")
        .then()
        .log().ifValidationFails(LogDetail.ALL)
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SECOND_STUDENT_GROUP_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");
  }

  @AfterAll
  static void deleteCreatedEntities() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/student-groups/{id}", firstStudentGroup)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/student-groups/{id}", secondStudentGroup)
        .then()
        .log().ifValidationFails(LogDetail.ALL)
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(gradeId))
        .delete("/grades")
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(studentId))
        .delete("/students")
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
  void getAllStudentGroups() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/student-groups")
        .then()
        .statusCode(200)
        //.body("size()", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getStudentGroupsWithTag() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("tags", List.of(tagId))
        .when()
        .get("/student-groups")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondStudentGroup))
        .log().ifValidationFails();
  }

  @Test
  void getStudentGroupsWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("search", FIRST_STUDENT_GROUP_NAME)
        .when()
        .get("/student-groups")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(firstStudentGroup))
        .log().ifValidationFails();
  }
}
