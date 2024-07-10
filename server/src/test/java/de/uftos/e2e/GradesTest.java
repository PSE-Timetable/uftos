package de.uftos.e2e;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GradesTest {
  private static final String FIRST_GRADE_NAME = "5";
  private static final String SECOND_GRADE_NAME = "7";
  private static final String TAG_NAME = "Nachmittagsunterricht";
  private static final String STUDENT_GROUP_NAME = "7-Ethik";

  static String firstGrade;
  static String secondGrade;
  static String tagId;
  static String studentGroupId;


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
        .body(generateGradeJson(FIRST_GRADE_NAME, Collections.emptyList(),
            Collections.emptyList()))
        .when()
        .post("/grades")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(FIRST_GRADE_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    secondGrade = given().contentType(ContentType.JSON)
        .body(generateGradeJson(SECOND_GRADE_NAME, List.of(studentGroupId),
            List.of(tagId)))
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
  static void deleteCreatedGrades() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/grades/{id}", firstGrade)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/grades/{id}", secondGrade)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/tags/{id}", tagId)
        .then()
        .statusCode(200);
  }


  private static String generateGradeJson(String name, List<String> studentGroups,
                                          List<String> tags)
      throws JSONException {
    return new JSONObject()
        .put("name", name)
        .put("studentGroups", studentGroups)
        .put("tags", tags)
        .toString();
  }

  private static String generateTagJson(String name)
      throws JSONException {
    return new JSONObject()
        .put("tagName", name)
        .toString();
  }

  private static String generatePageJson(int page, int size, List<String> sort)
      throws JSONException {
    return new JSONObject()
        .put("page", page)
        .put("size", size)
        .put("sort", sort)
        .toString();
  }


  @Test
  void getAllGrades() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/grades")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getGradesWithTag() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("tags", List.of(tagId))
        .when()
        .get("/grades")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondGrade))
        .log().ifValidationFails();
  }

  @Test
  void getGradesWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("name", FIRST_GRADE_NAME)
        .when()
        .get("/grades")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(firstGrade))
        .log().ifValidationFails();

  }

}
