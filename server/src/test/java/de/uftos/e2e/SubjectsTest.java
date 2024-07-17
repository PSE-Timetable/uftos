package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateSubjectJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Collections;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SubjectsTest {

  private static final String SUBJECT1_NAME = "Subject 1";
  private static final String SUBJECT2_NAME = "Subject 2";

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
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/subjects")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getSubjectsWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("name", "ct 1")
        .when()
        .get("/subjects")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(subject1Id))
        .log().ifValidationFails();
  }
}
