package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateIdListJson;
import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateRoomJson;
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

class RoomsTest {

  private static final String FIRST_ROOM_NAME = "100";
  private static final String FIRST_ROOM_BUILDING = "10.10";
  private static final int FIRST_ROOM_CAPACITY = 30;
  private static final String SECOND_ROOM_NAME = "101";
  private static final String SECOND_BUILDING_NAME = "11.11";
  private static final int SECOND_ROOM_CAPACITY = 50;
  private static final String TAG_NAME = "Accessible by wheelchair";

  static String firstRoomId;
  static String secondRoomId;
  static String tagId;

  @BeforeAll
  static void createTestRooms() throws JSONException {
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

    firstRoomId = given().contentType(ContentType.JSON)
        .body(generateRoomJson(FIRST_ROOM_NAME, FIRST_ROOM_BUILDING, FIRST_ROOM_CAPACITY,
            Collections.emptyList()))
        .when()
        .post("/rooms")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(FIRST_ROOM_NAME))
        .body("buildingName", equalTo(FIRST_ROOM_BUILDING))
        .body("capacity", equalTo(FIRST_ROOM_CAPACITY))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    secondRoomId = given().contentType(ContentType.JSON)
        .body(generateRoomJson(SECOND_ROOM_NAME, SECOND_BUILDING_NAME, SECOND_ROOM_CAPACITY,
            List.of(tagId)))
        .when()
        .post("/rooms")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(SECOND_ROOM_NAME))
        .body("buildingName", equalTo(SECOND_BUILDING_NAME))
        .body("capacity", equalTo(SECOND_ROOM_CAPACITY))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

  }

  @AfterAll
  static void deleteCreatedEntities() {
    given().contentType(ContentType.JSON)
        .when()
        .body(generateIdListJson(firstRoomId, secondRoomId))
        .delete("/rooms")
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
  void getAllRooms() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/rooms")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getRoomsWithTag() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("tags", List.of(tagId))
        .when()
        .get("/rooms")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondRoomId))
        .log().ifValidationFails();
  }

  @Test
  void getRoomsWithName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("search", FIRST_ROOM_NAME)
        .when()
        .get("/rooms")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(firstRoomId))
        .log().ifValidationFails();
  }

  @Test
  void getRoomsWithBuildingName() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("search", FIRST_ROOM_BUILDING)
        .when()
        .get("/rooms")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(firstRoomId))
        .log().ifValidationFails();

  }
}
