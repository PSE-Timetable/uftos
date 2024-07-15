package de.uftos.utils;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonGenerator {

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

  public static String generateSubjectJson(String name)
      throws JSONException {
    return new JSONObject()
        .put("name", name)
        .put("tagIds", new JSONArray())
        .toString();
  }

  public static String generateTagJson(String name)
      throws JSONException {
    return new JSONObject()
        .put("tagName", name)
        .toString();
  }

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
