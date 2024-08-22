package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateSubjectJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SubjectsTest {

  private static final String SUBJECT1_NAME = "Subject1";
  private static final String SUBJECT2_NAME = "Subject2";

  static String subject1Id;
  static String subject2Id;

  @BeforeAll
  static void createTestStudents() throws JSONException {
    subject1Id = given().contentType(ContentType.JSON)
        .body(generateSubjectJson(SUBJECT1_NAME))
        .when()
        .post("/subjects")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SUBJECT1_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    subject2Id = given().contentType(ContentType.JSON)
        .body(generateSubjectJson(SUBJECT2_NAME))
        .when()
        .post("/subjects")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SUBJECT2_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");
  }

  @AfterAll
  static void deleteCreatedStudents() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/subjects/{id}", subject1Id)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/subjects/{id}", subject2Id)
        .then()
        .statusCode(200);
  }

  @Test
  void getAllSubjects() throws JSONException {
    given().contentType(ContentType.JSON)
        .when()
        .get("/subjects")
        .then()
        .statusCode(200)
        .body("size()", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getSubjectsWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .param("search", SUBJECT1_NAME)
        .when()
        .get("/subjects")
        .then()
        .log().ifValidationFails(LogDetail.BODY)
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("[0].id", equalTo(subject1Id))
        .log().ifValidationFails();
  }
}
