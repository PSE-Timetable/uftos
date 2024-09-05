package de.uftos.utils;

import de.uftos.dto.requestdtos.LessonsCountRequestDto;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import java.util.Map.Entry;
import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("This class is only used to generate data on demand.")
public class DataGenerator {

  private static final List<String> firstNames = List.of("John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Laura", "Daniel", "Sophia");
  private static final List<String> lastNames = List.of("Smith", "Johnson", "Brown", "Williams", "Jones", "Garcia", "Miller", "Davis", "Martinez", "Lopez");
  private static final List<String> subjects = List.of("Math", "English", "History", "Science", "Art", "Physical Education");
  private static final List<String> colors = List.of("#FF5733", "#33FF57", "#3357FF", "#F333FF", "#FF3333");

  private static List<String> tags = new ArrayList<>();
  private static List<String> students = new ArrayList<>();
  private static List<String> teachers = new ArrayList<>();
  private static List<String> rooms = new ArrayList<>();
  private static List<String> studentGroups = new ArrayList<>();
  private static List<String> grades = new ArrayList<>();
  private static List<String> subjectIds = new ArrayList<>();
  private static final Map<String, String> gradeMap = new HashMap<>();
  private static final Map<String, String> gradeCurriculumMap = new HashMap<>();

  private static final Random random = new Random();

  // Helper methods to get random names
  private static String getRandomFirstName() {
    return firstNames.get(random.nextInt(firstNames.size()));
  }

  private static String getRandomLastName() {
    return lastNames.get(random.nextInt(lastNames.size()));
  }

  // Helper method to get random tag
  private static List<String> getRandomTag() {
    if (tags.isEmpty() || random.nextFloat() > 0.2) {
      return Collections.emptyList();
    }
    return List.of(tags.get(random.nextInt(tags.size())));
  }

  @Test
  @Order(1)
  public void createTags() throws JSONException {
    List<String> tagNames = List.of("blind", "deaf", "wheelchair");

    for (String tagName : tagNames) {
      String tagId = given().contentType(ContentType.JSON)
          .body(JsonGenerator.generateTagJson(tagName))
          .when()
          .post("/tags")
          .then()
          .statusCode(200)
          .body("id", notNullValue())
          .body("name", equalTo(tagName))
          .log().ifValidationFails()
          .extract()
          .body().jsonPath().getString("id");
      tags.add(tagId);
    }
  }

  @Test
  @Order(2)
  public void createSubjects() throws JSONException {
    for (String subjectName : subjects) {
      String color = colors.get(random.nextInt(colors.size()));
      String subjectId = given().contentType(ContentType.JSON)
          .body(JsonGenerator.generateSubjectJson(subjectName, color))
          .when()
          .post("/subjects")
          .then()
          .statusCode(200)
          .body("id", notNullValue())
          .body("name", equalTo(subjectName))
          .log().ifValidationFails()
          .extract()
          .body().jsonPath().getString("id");
      subjectIds.add(subjectId);
    }
  }

  @Test
  @Order(3)
  public void createGrades() throws JSONException {
    for (int i = 5; i <= 10; i++) {
      String gradeName = i + "th Grade";

      // Create grade and extract curriculumId
      String response = given().contentType(ContentType.JSON)
          .body(JsonGenerator.generateGradeJson(gradeName, null, new ArrayList<>(), new ArrayList<>()))
          .when()
          .post("/grades")
          .then()
          .statusCode(200)
          .body("id", notNullValue())
          .body("name", equalTo(gradeName))
          .log().ifValidationFails()
          .extract()
          .body().asString();

      String gradeId = JsonPath.from(response).getString("id");
      String curriculumId = JsonPath.from(response).getString("curriculumId");

      // Save gradeId and curriculumId in the map
      gradeCurriculumMap.put(gradeId, curriculumId);
      gradeMap.put(gradeId, gradeName);
      grades.add(gradeId);
    }
  }

  @Test
  @Order(4)
  public void createStudents() throws JSONException {
    for (int i = 1; i <= 50; i++) {
      String firstName = getRandomFirstName();
      String lastName = getRandomLastName();
      List<String> studentTags = getRandomTag();

      String studentId = given().contentType(ContentType.JSON)
          .body(
              JsonGenerator.generateStudentJson(firstName, lastName, new ArrayList<>(), studentTags))
          .when()
          .post("/students")
          .then()
          .statusCode(200)
          .body("id", notNullValue())
          .body("firstName", equalTo(firstName))
          .body("lastName", equalTo(lastName))
          .log().ifValidationFails()
          .extract()
          .body().jsonPath().getString("id");
      students.add(studentId);
    }
  }

  @Test
  @Order(5)
  public void createStudentGroups() throws JSONException {
    for (int i = 1; i <= 10; i++) {
      String assignedGradeId = grades.get(random.nextInt(grades.size()));
      String assignedGradeName = gradeMap.get(assignedGradeId);  // Get the gradeName from the map

      // Assign 1 or 2 unique subjects
      Set<String> assignedSubjects = new HashSet<>();
      while (assignedSubjects.size() < random.nextInt(2) + 1) {
        assignedSubjects.add(subjectIds.get(random.nextInt(subjectIds.size())));
      }

      String groupName = assignedSubjects.stream()
          .map(subjectId -> subjects.get(subjectIds.indexOf(subjectId)))
          .collect(Collectors.joining(", ")) + " for " + assignedGradeName;  // Use gradeName in group name

      List<String> groupTags = getRandomTag();

      // First, create the group without students
      String groupId = given().contentType(ContentType.JSON)
          .body(JsonGenerator.generateStudentGroupJson(groupName, new ArrayList<>(), List.of(assignedGradeId), groupTags, new ArrayList<>(assignedSubjects)))
          .when()
          .post("/student-groups")
          .then()
          .statusCode(200)
          .body("id", notNullValue())
          .body("name", equalTo(groupName))
          .log().ifValidationFails()
          .extract()
          .body().jsonPath().getString("id");

      studentGroups.add(groupId);

      // Then, assign students to the group
      List<String> groupStudents = IntStream.range(0, 5)
          .mapToObj(index -> students.get(random.nextInt(students.size())))
          .collect(Collectors.toList());

      given().contentType(ContentType.JSON)
          .body(groupStudents)
          .when()
          .post("/student-groups/" + groupId + "/students")
          .then()
          .statusCode(200)
          .body("id", equalTo(groupId))
          .log().ifValidationFails();
    }
  }

  @Test
  @Order(6)
  public void createTeachers() throws JSONException {
    for (int i = 1; i <= 20; i++) {
      String firstName = getRandomFirstName();
      String lastName = getRandomLastName();
      String acronym = firstName.charAt(0) + "" + lastName.charAt(0);
      List<String> teacherTags = getRandomTag();

      // Assign 2 or 3 random subjects to each teacher
      Set<String> assignedSubjects = new HashSet<>();
      while (assignedSubjects.size() < random.nextInt(2) + 2) {
        assignedSubjects.add(subjectIds.get(random.nextInt(subjectIds.size())));
      }

      String teacherId = given().contentType(ContentType.JSON)
          .body(JsonGenerator.generateTeacherJson(firstName, lastName, acronym, new ArrayList<>(assignedSubjects), teacherTags))
          .when()
          .post("/teachers")
          .then()
          .statusCode(200)
          .body("id", notNullValue())
          .body("firstName", equalTo(firstName))
          .body("lastName", equalTo(lastName))
          .log().ifValidationFails()
          .extract()
          .body().jsonPath().getString("id");

      teachers.add(teacherId);
    }
  }

  @Test
  @Order(7)
  public void createRooms() throws JSONException {
    for (int i = 1; i <= 10; i++) {
      String roomName = "Room " + i;
      String buildingName = "Building A";
      int capacity = 30 + random.nextInt(20);
      List<String> roomTags = getRandomTag();

      String roomId = given().contentType(ContentType.JSON)
          .body(JsonGenerator.generateRoomJson(roomName, buildingName, capacity, roomTags))
          .when()
          .post("/rooms")
          .then()
          .statusCode(200)
          .body("id", notNullValue())
          .body("name", equalTo(roomName))
          .log().ifValidationFails()
          .extract()
          .body().jsonPath().getString("id");
      rooms.add(roomId);
    }
  }

  /*
  This doesn't currently work, idk why.
  {
    "timestamp": "2024-09-05T10:54:08.090+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "No static resource curriculum.",
    "path": "/curriculum/"
  }

  @Test
  @Order(8)
  public void updateCurricula() throws JSONException {
    for (Entry<String, String> idEntry : gradeCurriculumMap.entrySet()) {
      String gradeId = idEntry.getKey();
      String curriculumId = idEntry.getValue();

      // Generate lesson counts for each subject
      List<LessonsCountRequestDto> lessonCounts = subjects.stream()
          .map(subject -> new LessonsCountRequestDto(subjectIds.get(subjects.indexOf(subject)), random.nextInt(3) + 1))
          .collect(Collectors.toList());

      String curriculumName = gradeMap.get(gradeId);
      given().contentType(ContentType.JSON)
          .when()
          .body(JsonGenerator.generateCurriculumJson(gradeId, curriculumName, lessonCounts))
          .put("/curriculum/" + curriculumId)
          .then()
          .log().everything()
          .statusCode(200);
    }
  }
  */
}
