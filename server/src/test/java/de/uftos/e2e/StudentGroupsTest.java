package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateGradeJson;
import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateStudentGroupJson;
import static de.uftos.utils.JsonGenerator.generateStudentJson;
import static de.uftos.utils.JsonGenerator.generateTagJson;
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

@SuppressWarnings("checkstyle:MissingJavadocType")
public class StudentGroupsTest {
  private static final String FIRST_STUDENT_GROUP_NAME = "5-Ethik";
  private static final String SECOND_STUDENT_GROUP_NAME = "5-Religion";
  private static final String STUDENT_FIRST_NAME = "Max";
  private static final String STUDENT_LAST_NAME = "Mustermann";
  private static final String GRADE_NAME = "5";
  private static final String TAG_NAME = "Sehbehinderung";

  static String firstStudentGroup;
  static String secondStudentGroup;
  static String studentId;
  static String gradeId;
  static String tagId;


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

    studentId = given().contentType(ContentType.JSON)
        .body(generateStudentJson(STUDENT_FIRST_NAME, STUDENT_LAST_NAME, Collections.emptyList()))
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

    System.out.println(
        generateStudentJson(STUDENT_FIRST_NAME, STUDENT_LAST_NAME, Collections.emptyList()));

    gradeId = given().contentType(ContentType.JSON)
        .body(generateGradeJson(GRADE_NAME, Collections.emptyList(), Collections.emptyList()))
        .when()
        .post("/grades")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(GRADE_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    System.out.println(generateStudentGroupJson(FIRST_STUDENT_GROUP_NAME, List.of(studentId),
        Collections.emptyList(), Collections.emptyList()));

    firstStudentGroup = given().contentType(ContentType.JSON)
        .body(generateStudentGroupJson(FIRST_STUDENT_GROUP_NAME, Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList()))
        .when()
        .post("/student-groups")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(FIRST_STUDENT_GROUP_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    secondStudentGroup = given().contentType(ContentType.JSON)
        .body(generateStudentGroupJson(SECOND_STUDENT_GROUP_NAME, List.of(studentId),
            List.of(gradeId), List.of(tagId)))
        .when()
        .post("/student-groups")
        .then()
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
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/tags/{id}", tagId)
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
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getStudentsWithTag() throws JSONException {
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
  void getStudentsWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("name", FIRST_STUDENT_GROUP_NAME)
        .when()
        .get("/student-groups")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(firstStudentGroup))
        .log().ifValidationFails();
  }
}
