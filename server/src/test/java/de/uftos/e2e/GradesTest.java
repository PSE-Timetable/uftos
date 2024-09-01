package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateCurriculumJson;
import static de.uftos.utils.JsonGenerator.generateGradeJson;
import static de.uftos.utils.JsonGenerator.generateIdListJson;
import static de.uftos.utils.JsonGenerator.generateTagJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
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

class GradesTest {
  private static final String FIRST_GRADE_NAME = "5";
  private static final String SECOND_GRADE_NAME = "7";
  private static final String FIRST_CURRICULUM_NAME = "curriculum5";
  private static final String SECOND_CURRICULUM_NAME = "curriculum7";
  private static final String TAG_NAME = "Afternoon lessons";

  static String firstGrade;
  static String secondGrade;
  static String firstCurriculum;
  static String secondCurriculum;
  static String tagId;

  @BeforeAll
  static void createTestGrades() throws JSONException {
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

    firstGrade = given().contentType(ContentType.JSON)
        .body(generateGradeJson(FIRST_GRADE_NAME, null, Collections.emptyList(),
            Collections.emptyList()))
        .when()
        .post("/grades")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(FIRST_GRADE_NAME))
        .log().ifValidationFails(LogDetail.BODY)
        .extract()
        .body().jsonPath().getString("id");

    secondGrade = given().contentType(ContentType.JSON)
        .body(generateGradeJson(SECOND_GRADE_NAME, null, Collections.emptyList(),
            List.of(tagId)))
        .when()
        .post("/grades")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SECOND_GRADE_NAME))
        .log().ifValidationFails(LogDetail.BODY)
        .extract()
        .body().jsonPath().getString("id");
  }

  @AfterAll
  static void deleteCreatedEntities() {

    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(firstGrade, secondGrade))
        .delete("/grades")
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
  void getAllGrades() {
    given().contentType(ContentType.JSON)
        .body("")
        .when()
        .get("/grades")
        .then()
        .statusCode(200)
        .body("size()", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getGradesWithTag() {
    given().contentType(ContentType.JSON)
        .body("")
        .param("tags", List.of(tagId))
        .when()
        .get("/grades")
        .then()
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("id", contains(secondGrade))
        .log().ifValidationFails();
  }

  @Test
  void getGradesWithName() {
    given().contentType(ContentType.JSON)
        .body("")
        .param("search", FIRST_GRADE_NAME)
        .when()
        .get("/grades")
        .then()
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("id", contains(firstGrade))
        .log().ifValidationFails();
  }
}
