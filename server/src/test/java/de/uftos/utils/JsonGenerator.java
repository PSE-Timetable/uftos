package de.uftos.utils;

import de.uftos.dto.Weekday;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class helps to generate the JSON needed for the E2E tests.
 */
public class JsonGenerator {

  /**
   * Generates the student JSON.
   *
   * @param firstName The first name of the student
   * @param lastName  The last name of the student
   * @param tags      The ids of the tags the student has
   * @return The requested JSON
   * @throws JSONException If something is malformed.
   */
  public static String generateStudentJson(String firstName, String lastName, List<String> tags)
      throws JSONException {
    JSONArray jsonArray = new JSONArray();
    tags.forEach(jsonArray::put);
    return new JSONObject()
        .put("firstName", firstName)
        .put("lastName", lastName)
        .put("tagIds", jsonArray)
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
