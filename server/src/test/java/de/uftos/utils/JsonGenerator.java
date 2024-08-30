package de.uftos.utils;

import de.uftos.dto.Weekday;
import de.uftos.dto.requestdtos.LessonsCountRequestDto;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class helps to generate the JSON needed for the E2E tests.
 */
public class JsonGenerator {

  /**
   * Generates the grade JSON.
   *
   * @param name          The name of the grade
   * @param studentGroups The ids of the student groups the grade contains
   * @param tags          The ids of the tags the grade has
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateGradeJson(String name, String curriculumId,
                                         List<String> studentGroups,
                                         List<String> tags)
      throws JSONException {
    JSONArray tagIds = new JSONArray();
    tags.forEach(tagIds::put);

    JSONArray studentGroupIds = new JSONArray();
    studentGroups.forEach(studentGroupIds::put);
    return new JSONObject()
        .put("name", name)
        .put("curriculumId", curriculumId)
        .put("studentGroupIds", studentGroupIds)
        .put("tagIds", tagIds)
        .toString();
  }

  /**
   * Generates the teacher JSON.
   *
   * @param firstName  The first name of the teacher
   * @param lastName   The last name of the teacher
   * @param acronym    The acronym of the teacher
   * @param subjectIds The IDs of the subjects the teacher can teach
   * @param tags       The IDs of the tags the teacher has
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateTeacherJson(String firstName, String lastName, String acronym,
                                           List<String> subjectIds, List<String> tags)
      throws JSONException {
    JSONArray tagsArray = new JSONArray();
    tags.forEach(tagsArray::put);
    JSONArray subjectsArray = new JSONArray();
    subjectIds.forEach(subjectsArray::put);
    return new JSONObject()
        .put("firstName", firstName)
        .put("lastName", lastName)
        .put("acronym", acronym)
        .put("subjectIds", subjectsArray)
        .put("tagIds", tagsArray)
        .toString();
  }

  /**
   * Generates the grade JSON.
   *
   * @param gradeId       The id of the curriculum's grade
   * @param name          The name of the curriculum
   * @param lessonsCounts The number of lessons for each subject
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateCurriculumJson(String gradeId, String name,
                                              List<LessonsCountRequestDto> lessonsCounts)
      throws JSONException {
    JSONArray jsonArray = new JSONArray();
    for (LessonsCountRequestDto dto : lessonsCounts) {
      JSONObject dtoJson = generateLessonsCountJson(dto);
      jsonArray.put(dtoJson);
    }
    return new JSONObject()
        .put("gradeId", gradeId)
        .put("name", name)
        .put("lessonsCounts", jsonArray)
        .toString();
  }

  /**
   * Generates the grade JSON.
   *
   * @param dto The lesson count DTO that should be transformed to a JSON
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static JSONObject generateLessonsCountJson(LessonsCountRequestDto dto)
      throws JSONException {
    return new JSONObject()
        .put("subjectId", dto.subjectId())
        .put("count", dto.count());
  }

  /**
   * Generates the student JSON.
   *
   * @param name         The name of the room
   * @param buildingName The name of the building the room is in
   * @param capacity     The maximum number of people the room is suitable for
   * @param tags         The ids of the tags the student has
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateRoomJson(String name, String buildingName, int capacity,
                                        List<String> tags) throws JSONException {
    JSONArray jsonArray = new JSONArray();
    tags.forEach(jsonArray::put);
    return new JSONObject()
        .put("name", name)
        .put("buildingName", buildingName)
        .put("capacity", capacity)
        .put("tagIds", jsonArray)
        .toString();
  }

  /**
   * Generates the student JSON.
   *
   * @param firstName The first name of the student
   * @param lastName  The last name of the student
   * @param tags      The ids of the tags the student has
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateStudentJson(String firstName, String lastName, List<String> groups,
                                           List<String> tags)
      throws JSONException {
    JSONArray groupArray = new JSONArray();
    groups.forEach(groupArray::put);

    JSONArray tagArray = new JSONArray();
    tags.forEach(tagArray::put);


    return new JSONObject()
        .put("firstName", firstName)
        .put("lastName", lastName)
        .put("groupIds", groupArray)
        .put("tagIds", tagArray)
        .toString();
  }

  /**
   * Generates the student JSON.
   *
   * @param name     The name of the student group
   * @param students The ids of the students the student group contains
   * @param grades   The ids of the grades the student group belongs to
   * @param tags     The ids of the tags the student group has
   * @param subjects The ids of the subject the student group has
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateStudentGroupJson(String name, List<String> students,
                                                List<String> grades, List<String> tags,
                                                List<String> subjects)
      throws JSONException {

    JSONArray studentIds = new JSONArray();
    students.forEach(studentIds::put);

    JSONArray gradeIds = new JSONArray();
    grades.forEach(gradeIds::put);

    JSONArray tagIds = new JSONArray();
    tags.forEach(tagIds::put);

    JSONArray subjectIds = new JSONArray();
    subjects.forEach(subjectIds::put);

    return new JSONObject()
        .put("name", name)
        .put("studentIds", studentIds)
        .put("gradeIds", gradeIds)
        .put("tagIds", tagIds)
        .put("subjectIds", subjectIds)
        .toString();
  }

  /**
   * Generates the subject JSON.
   *
   * @param name The name of the subject
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateSubjectJson(String name)
      throws JSONException {
    return new JSONObject()
        .put("name", name)
        .put("tagIds", new JSONArray())
        .toString();
  }

  /**
   * Generates the tag JSON.
   *
   * @param name The name of the tag
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateTagJson(String name)
      throws JSONException {
    return new JSONObject()
        .put("tagName", name)
        .toString();
  }

  /**
   * Generates a JSON with a list of ids.
   *
   * @param ids The ids of the entities
   * @return The requested JSON
   */
  public static String generateIdListJson(String... ids) {
    JSONArray jsonArray = new JSONArray();
    for (String id : ids) {
      jsonArray.put(id);
    }

    return jsonArray.toString();
  }

  /**
   * Generates the timeslot JSON.
   *
   * @param day  The weekday of the timeslot
   * @param slot The index of the timeslot within the day
   * @param tags The ids of the tags the timeslot has
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateTimeslotJson(Weekday day, int slot, List<String> tags)
      throws JSONException {
    JSONArray jsonArray = new JSONArray();
    tags.forEach(jsonArray::put);

    return new JSONObject()
        .put("day", day)
        .put("slot", slot)
        .put("tagIds", jsonArray)
        .toString();
  }


  /**
   * Generates the page JSON.
   *
   * @param page The page number
   * @param size The size of the page
   * @param sort How the data should be sorted
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generatePageJson(int page, int size, List<String> sort)
      throws JSONException {
    JSONArray jsonArray = new JSONArray();
    sort.forEach(jsonArray::put);
    return new JSONObject()
        .put("page", page)
        .put("size", size)
        .put("sort", jsonArray)
        .toString();
  }
}
