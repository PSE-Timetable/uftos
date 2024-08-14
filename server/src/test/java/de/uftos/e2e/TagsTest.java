package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateTagJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.ContentType;
import java.util.Collections;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TagsTest {
  private static final String FIRST_TAG_NAME = "Visual Impairment";
  private static final String SECOND_TAG_NAME = "Wheelchair";

  static String tagId1;
  static String tagId2;

  @BeforeAll
  static void createTestTags() throws JSONException {
    tagId1 = given().contentType(ContentType.JSON)
        .body(generateTagJson(FIRST_TAG_NAME))
        .when()
        .post("/tags")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(FIRST_TAG_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    tagId2 = given().contentType(ContentType.JSON)
        .body(generateTagJson(SECOND_TAG_NAME))
        .when()
        .post("/tags")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SECOND_TAG_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");
  }

  @AfterAll
  static void deleteCreatedTags() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/tags/{id}", tagId1)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/tags/{id}", tagId2)
        .then()
        .statusCode(200);
  }

  @Test
  void getAllTags() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("size()", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getTagsWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("name", FIRST_TAG_NAME)
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("size()", equalTo(1))
        .body("id", contains(tagId1))
        .log().ifValidationFails();

  }

}
