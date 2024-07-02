package de.uftos.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StudentControllerTest {

  static String jochenId;
  static String jasonId;

  @BeforeAll
  static void createTestStudents() {
    jochenId = given().contentType(ContentType.JSON)
        .body("""
                    {
                      "firstName": "Jochen",
                      "lastName": "Musterman",
                      "tagIds": []
                    }""")
        .when()
        .post("/students")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo("Jochen"))
        .body("lastName", equalTo("Musterman"))
        .extract()
        .body().jsonPath().getString("id");

    jasonId = given().contentType(ContentType.JSON)
        .body("""
                    {
                      "firstName": "Jason",
                      "lastName": "Musterman",
                      "tagIds": []
                    }""")
        .when()
        .post("/students")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo("Jason"))
        .body("lastName", equalTo("Musterman"))
        .extract()
        .body().jsonPath().getString("id");
  }

  @Test
  void getStudentsWithNameTest() {
    given().contentType(ContentType.JSON)
        .body("""
                    {
                      "page": 0,
                      "size": 10,
                      "sort": [
                        "string"
                      ]
                    }""")
        .param("firstName", "Jochen")
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(jochenId))
        .log()
        .ifValidationFails();
  }

  @AfterAll
  static void deleteCreatedStudents() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/students/{id}", jasonId)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/students/{id}", jochenId)
        .then()
        .statusCode(200);
  }
}