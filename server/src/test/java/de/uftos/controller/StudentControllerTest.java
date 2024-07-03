package de.uftos.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StudentControllerTest {

  static String karenId;
  static String jasonId;
  static String tagId;

  @BeforeAll
  static void createTestStudents() {
    tagId = given().contentType(ContentType.JSON)
        .body("""
                    {
                      "tagName": "Sehebehinderung"
                    }""")
        .when()
        .post("/tags")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo("Sehebehinderung"))
        .extract()
        .body().jsonPath().getString("id");
    System.out.println("tag id: " + tagId);

    StringBuilder builder = new StringBuilder();
    builder.append("{").append("\"firstName\": \"Jason\",").append("\"lastName\": \"Musterman\",").append("\"tagIds\": [\"").append(tagId).append("\"]}");
    jasonId = given().contentType(ContentType.JSON)
        .body(builder.toString())
        .when()
        .post("/students")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo("Jason"))
        .body("lastName", equalTo("Musterman"))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    karenId = given().contentType(ContentType.JSON)
        .body("""
                    {
                      "firstName": "Karen",
                      "lastName": "Musterman",
                      "tagIds": []
                    }""")
        .when()
        .post("/students")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo("Karen"))
        .body("lastName", equalTo("Musterman"))
        .extract()
        .body().jsonPath().getString("id");
  }

  @Test
  void getAllStudentsTest() {
    given().contentType(ContentType.JSON)
        .body("""
                    {
                      "page": 0,
                      "size": 10,
                      "sort": [
                        "string"
                      ]
                    }""")
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log()
        .ifValidationFails();
  }

  @Test
  void getStudentsWithTagTest() {
    given().contentType(ContentType.JSON)
        .body("""
                    {
                      "page": 0,
                      "size": 10,
                      "sort": [
                        "string"
                      ]
                    }""")
        .param("tags", List.of(tagId))
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        //.body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(jasonId))
        .log()
        .ifValidationFails();
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
        .param("firstName", "Karen")
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        //.body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(karenId))
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
        .delete("/students/{id}", karenId)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/tags/{id}", tagId)
        .then()
        .statusCode(200);
  }
}