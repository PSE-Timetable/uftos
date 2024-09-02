package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateGradeJson;
import static de.uftos.utils.JsonGenerator.generateIdListJson;
import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateSubjectJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import java.util.Collections;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CurriculumTest {
  private static final String FIRST_CURRICULUM_NAME = "5 / SS24";
  private static final String SECOND_CURRICULUM_NAME = "7 / SS24";

  private static final String SUBJECT_NAME = "Maths";

  private static final String FIRST_GRADE_NAME = "5";
  private static final String SECOND_GRADE_NAME = "7";


  static String firstGradeId;
  static String secondGradeId;

  static String subjectId;

  @BeforeAll
  static void createTestCurricula() throws JSONException {

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

    firstGradeId = given().contentType(ContentType.JSON)
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

    secondGradeId = given().contentType(ContentType.JSON)
        .body(generateGradeJson(SECOND_GRADE_NAME, null, Collections.emptyList(),
            Collections.emptyList()))
        .when()
        .post("/grades")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SECOND_GRADE_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

  }

  @AfterAll
  static void deleteCreatedEntities() {

    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(firstGradeId, secondGradeId))
        .delete("/grades")
        .then()
        .log().ifValidationFails(LogDetail.BODY)
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(subjectId))
        .delete("/subjects")
        .then()
        .statusCode(200);
  }

  @Test
  void getAllCurricula() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/curriculum")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getCurriculumWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("name", FIRST_GRADE_NAME)
        .when()
        .get("/curriculum")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .log().ifValidationFails();
  }
}
