package de.uftos.e2e;

import static de.uftos.utils.JsonGenerator.generateGradeJson;
import static de.uftos.utils.JsonGenerator.generatePageJson;
import static de.uftos.utils.JsonGenerator.generateRoomJson;
import static de.uftos.utils.JsonGenerator.generateStudentJson;
import static de.uftos.utils.JsonGenerator.generateTagJson;
import static de.uftos.utils.JsonGenerator.generateTeacherJson;
import static de.uftos.utils.JsonGenerator.generateTimetableMetadataJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import de.uftos.entities.Break;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ServerTest {


  static String studentId;
  private static final String FIRST_STUDENT_FIRST_NAME = "Karen";
  private static final String FIRST_STUDENT_LAST_NAME = "Mustermann";

  static String teacherId;
  private static final String FIRST_TEACHER_FIRST_NAME = "Karen";
  private static final String FIRST_TEACHER_LAST_NAME = "Mustermann";
  private static final String FIRST_TEACHER_ACRONYM = "KM";

  static String gradeId;
  private static final String FIRST_GRADE_NAME = "5";

  static String roomId;
  private static final String FIRST_ROOM_NAME = "100";
  private static final String FIRST_ROOM_BUILDING = "10.10";
  private static final int FIRST_ROOM_CAPACITY = 30;

  @BeforeAll
  static void createTestEntities() throws JSONException {

    studentId = given().contentType(ContentType.JSON)
        .body(generateStudentJson(FIRST_STUDENT_FIRST_NAME, FIRST_STUDENT_LAST_NAME, List.of(),
            List.of()))
        .when()
        .post("/students")
        .then()
        .log().ifValidationFails(LogDetail.ALL)
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo(FIRST_STUDENT_FIRST_NAME))
        .body("lastName", equalTo(FIRST_STUDENT_LAST_NAME))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");

    teacherId = given().contentType(ContentType.JSON)
        .body(generateTeacherJson(FIRST_TEACHER_FIRST_NAME, FIRST_TEACHER_LAST_NAME,
            FIRST_TEACHER_ACRONYM, Collections.emptyList(), Collections.emptyList()))
        .when()
        .post("/teachers")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("firstName", equalTo(FIRST_TEACHER_FIRST_NAME))
        .body("lastName", equalTo(FIRST_TEACHER_LAST_NAME))
        .body("acronym", equalTo(FIRST_TEACHER_ACRONYM))
        .log().ifValidationFails()
        .extract()
        .body().jsonPath().getString("id");


    gradeId = given().contentType(ContentType.JSON)
        .body(generateGradeJson(FIRST_GRADE_NAME, null, Collections.emptyList(),
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

    roomId = given().contentType(ContentType.JSON)
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
  }

  @AfterAll
  static void deleteCreatedEntities() {
    given().contentType(ContentType.JSON)
        .when()
        .delete("/students/{id}", studentId)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/teachers/{id}", teacherId)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/grades/{id}", gradeId)
        .then()
        .statusCode(200);

    given().contentType(ContentType.JSON)
        .when()
        .delete("/rooms/{id}", roomId)
        .then()
        .statusCode(200);
  }

  @AfterEach
  void restoreDefaultValues() throws JSONException {
    //restores default values of timetable metadata
    given().contentType(ContentType.JSON)
        .body(generateTimetableMetadataJson(45, 0, "07:45", List.of()))
        .when()
        .put("/server/timetable-metadata")
        .then()
        .statusCode(200)
        .log().ifValidationFails();
  }


  @Test
  void getDefaultMetadata() throws JSONException {
    given().contentType(ContentType.JSON)
        .when()
        .get("/server/timetable-metadata")
        .then()
        .statusCode(200)
        .body("timeslotLength", equalTo(45))
        .body("timeslotsAmount", equalTo(0))
        .body("startTime", equalTo("07:45"))
        .log().ifValidationFails();
  }

  @Test
  void getStats() throws JSONException {
    given().contentType(ContentType.JSON)
        .when()
        .get("/server/statistics")
        .then()
        .statusCode(200)
        .body("constraintCount", equalTo(0))
        .body("teacherCount", equalTo(1))
        .body("gradeCount", equalTo(1))
        .body("roomCount", equalTo(1))
        .body("studentCount", equalTo(1))
        .log().ifValidationFails();
  }

  @Test
  void setMetadata() throws JSONException {
    Break b = new Break(false, 0, 15);

    given().contentType(ContentType.JSON)
        .body(generateTimetableMetadataJson(90, 10, "08:00", List.of(b)))
        .when()
        .put("/server/timetable-metadata")
        .then()
        .statusCode(200)
        .log().ifValidationFails();
  }
}
