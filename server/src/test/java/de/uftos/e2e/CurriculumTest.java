package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateCurriculumJson;
import static de.uftos.utils.JsonGenerator.generateGradeJson;
import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateSubjectJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import de.uftos.dto.LessonsCountRequestDto;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CurriculumTest {
  private static final String FIRST_CURRICULUM_NAME = "5 / SS24";
  private static final String SECOND_CURRICULUM_NAME = "7 / SS24";

  private static final String SUBJECT_NAME = "Mathe";

  private static final String FIRST_GRADE_NAME = "5";
  private static final String SECOND_GRADE_NAME = "7";


  static String firstGradeId;
  static String secondGradeId;

  static String subjectId;

  static String firstCurriculum;
  static String secondCurriculum;

  @BeforeAll
  static void createTestCurricula() throws JSONException {

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

    firstGradeId = given().contentType(ContentType.JSON)
        .body(generateGradeJson(FIRST_GRADE_NAME, Collections.emptyList(),
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
        .body(generateGradeJson(SECOND_GRADE_NAME, Collections.emptyList(),
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

    firstCurriculum = given().contentType(ContentType.JSON)
        .body(generateCurriculumJson(firstGradeId, FIRST_CURRICULUM_NAME,
            Collections.emptyList()))
        .when()
        .post("/curriculum")
        .then()
        .log().ifValidationFails(LogDetail.BODY)
        .statusCode(200)
        .body("id", notNullValue())
        .body("grade.id", equalTo(firstGradeId))
        .body("name", equalTo(FIRST_CURRICULUM_NAME))
        .log().ifValidationFails(LogDetail.BODY)
        .extract()
        .body().jsonPath().getString("id");


    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto(subjectId, 3);

    secondCurriculum = given().contentType(ContentType.JSON)
        .body(generateCurriculumJson(secondGradeId, SECOND_CURRICULUM_NAME,
            List.of(lessonsCount)))
        .when()
        .post("/curriculum")
        .then()
        .log().ifValidationFails(LogDetail.BODY)
        .statusCode(200)
        .body("id", notNullValue())
        .body("grade.id", equalTo(secondGradeId))
        .body("name", equalTo(SECOND_CURRICULUM_NAME))
        .extract()
        .body().jsonPath().getString("id");
  }

  @AfterAll
  static void deleteCreatedEntities() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/curriculum/{id}", firstCurriculum)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/curriculum/{id}", secondCurriculum)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/grades/{id}", firstGradeId)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/grades/{id}", secondGradeId)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/subjects/{id}", subjectId)
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
        .param("name", FIRST_CURRICULUM_NAME)
        .when()
        .get("/curriculum")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(firstCurriculum))
        .log().ifValidationFails();

  }
}
