package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateRoomJson;
import static de.uftos.utils.JsonGenerator.generateStudentGroupJson;
import static de.uftos.utils.JsonGenerator.generateSubjectJson;
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

public class LessonsTest {

  private static final String SUBJECT1_NAME = "Mathe";
  private static final String ROOM1_NAME = "100";
  private static final String ROOM1_BUILDING = "10.10";
  private static final int ROOM1_CAPACITY = 30;
  private static final Weekday TIMESLOT1_DAY = Weekday.SATURDAY;
  private static final int TIMESLOT1_SLOT = 1;
  private static final String STUDENT_GROUP1_NAME = "5-Ethik";

  static String studentGroup1Id;
  static String teacher1Id;
  static String subject1Id;
  static String room1Id;
  static String timeslot1Id;
  static String secondStudent;
  static String tagId;

  @BeforeAll
  static void createTestLessons() throws JSONException {
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

    room1Id = given().contentType(ContentType.JSON)
        .body(generateRoomJson(ROOM1_NAME, ROOM1_BUILDING, ROOM1_CAPACITY, Collections.emptyList()))
        .when()
        .post("/rooms")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(ROOM1_NAME))
        .body("buildingName", equalTo(ROOM1_BUILDING))
        .body("capacity", equalTo(ROOM1_CAPACITY))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    timeslot1Id = given().contentType(ContentType.JSON)
        .body(generateTimeslotJson(TIMESLOT1_DAY, TIMESLOT1_SLOT, Collections.emptyList()))
        .when()
        .post("/timeslots")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("day", equalTo(TIMESLOT1_DAY.name()))
        .body("slot", equalTo(TIMESLOT1_SLOT))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    studentGroup1Id = given().contentType(ContentType.JSON)
        .body(generateStudentGroupJson(STUDENT_GROUP1_NAME, Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList()))
        .when()
        .post("/student-groups")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("name", equalTo(STUDENT_GROUP1_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");


  }

  @AfterAll
  static void deleteCreatedEntities() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/subjects/{id}", subject1Id)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/rooms/{id}", room1Id)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/timeslots/{id}", timeslot1Id)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/student-groups/{id}", studentGroup1Id)
        .then()
        .statusCode(200);
  }


  @Test
  void getAllLessons() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(2))
        .log().ifValidationFails();
  }

  @Test
  void getLessonsWithTimetable() throws JSONException {
    given().contentType(ContentType.JSON)
        .body(generatePageJson(0, 10, Collections.emptyList()))
        .param("tags", List.of(tagId))
        .when()
        .get("/students")
        .then()
        .statusCode(200)
        .body("totalElements", equalTo(1))
        .body("content[0].id", equalTo(secondStudent))
        .log().ifValidationFails();
  }
  
}
