package de.uftos.e2e;

import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class StudentGroupTest {

    private static final String FIRST_STUDENT_GROUP_NAME = "5A";
    private static final String SECOND_STUDENT_GROUP_NAME = "5B";
    private static final String TAG_NAME = "Sehbehinderung";


    static String firstStudentGroup;
    static String secondStudentGroup;
    static String tagId;

    @BeforeAll
    static void createTestStudentGroups() throws JSONException {

        firstStudentGroup = given().contentType(ContentType.JSON)
                .body(generateStudentGroupJson(FIRST_STUDENT_GROUP_NAME, List.of(tagId)))
                .when()
                .post("/studentGroups")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo(FIRST_STUDENT_GROUP_NAME))
                .log().ifValidationFails()
                .extract()
                .body().jsonPath().getString("id");


        secondStudentGroup = given().contentType(ContentType.JSON)
                .body(generateStudentGroupJson(SECOND_STUDENT_GROUP_NAME, List.of(tagId)))
                .when()
                .post("/studentGroups")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo(SECOND_STUDENT_GROUP_NAME))
                .log().ifValidationFails()
                .extract()
                .body().jsonPath().getString("id");

    }

    @AfterAll
    static void deleteCreatedStudents() {
        given().contentType(ContentType.JSON)
                .when()
                .delete("/studentGroups/{id}", firstStudentGroup)
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .delete("/studentGroups/{id}", secondStudentGroup)
                .then()
                .statusCode(200);
    }

    private static String generateStudentGroupJson(String name, List<String> tags)
            throws JSONException {
        return new JSONObject()
                .put("name", name)
                .put("tags", tags)
                .toString();
    }

//    private static String generateTagJson(String name)
//            throws JSONException {
//        return new JSONObject()
//                .put("tagName", name)
//                .toString();
//    }

    private static String generatePageJson(int page, int size, List<String> sort)
            throws JSONException {
        return new JSONObject()
                .put("page", page)
                .put("size", size)
                .put("sort", sort)
                .toString();
    }

    @Test
    void getAllStudentGroups() throws JSONException {
        given().contentType(ContentType.JSON)
                .body(generatePageJson(0, 10, Collections.emptyList()))
                .when()
                .get("/studentGroups")
                .then()
                .statusCode(200)
                .body("totalElements", equalTo(2))
                .log().ifValidationFails();
    }

    @Test
    void getStudentGroupsWithTag() throws JSONException {
        given().contentType(ContentType.JSON)
                .body(generatePageJson(0, 10, Collections.emptyList()))
                .param("tags", List.of(tagId))
                .when()
                .get("/studentGroups")
                .then()
                .statusCode(200)
                .body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(secondStudentGroup))
                .log().ifValidationFails();
    }

    @Test
    void getStudentGroupsWithName() throws JSONException {
        given().contentType(ContentType.JSON)
                .body(generatePageJson(0, 10, Collections.emptyList()))
                .param("name", FIRST_STUDENT_GROUP_NAME)
                .when()
                .get("/studentGroups")
                .then()
                .statusCode(200)
                .body("totalElements", equalTo(1))
                .body("content[0].id", equalTo(firstStudentGroup));
    }
}
