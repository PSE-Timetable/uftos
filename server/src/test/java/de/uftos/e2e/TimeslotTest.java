package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateSubjectJson;
import static de.uftos.utils.JsonGenerator.generateTagJson;
import static de.uftos.utils.JsonGenerator.generateTimeslotJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import de.uftos.dto.Weekday;
import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TimeslotTest {

  private static final String TAG_NAME = "Vormittags";
  private static final Weekday FIRST_TIMESLOT_DAY = Weekday.TUESDAY;
  private static final int FIRST_TIMESLOT_SLOT = 1;
  private static final Weekday SECOND_TIMESLOT_DAY = Weekday.FRIDAY;
  private static final int SECOND_TIMESLOT_SLOT = 2;

  static String firstTimeslot;
  static String secondTimeslot;
  static String tagId;

  @BeforeAll
  static void createTestStudents() throws JSONException {
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

    firstTimeslot = given().contentType(ContentType.JSON)
        .body(
            generateTimeslotJson(FIRST_TIMESLOT_DAY, FIRST_TIMESLOT_SLOT, Collections.emptyList()))
        .when()
        .post("/timeslots")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("day", equalTo(FIRST_TIMESLOT_DAY.name()))
        .body("slot", equalTo(FIRST_TIMESLOT_SLOT))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    secondTimeslot = given().contentType(ContentType.JSON)
        .body(
            generateTimeslotJson(SECOND_TIMESLOT_DAY, SECOND_TIMESLOT_SLOT, List.of(tagId)))
        .when()
        .post("/timeslots")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("day", equalTo(SECOND_TIMESLOT_DAY.name()))
        .body("slot", equalTo(SECOND_TIMESLOT_SLOT))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");
  }

  @AfterAll
  static void deleteCreatedEntities() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/timeslots/{id}", firstTimeslot)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/timeslots/{id}", secondTimeslot)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/tags/{id}", tagId)
        .then()
        .statusCode(200);
  }

  @Test
  void getAllTimeslots() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/timeslots")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getTimeslotsWithTag() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("tags", List.of(tagId))
        .when()
        .get("/timeslots")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondTimeslot))
        .log().ifValidationFails();
  }

}
