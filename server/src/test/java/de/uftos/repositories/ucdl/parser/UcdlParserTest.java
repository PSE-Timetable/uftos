package de.uftos.repositories.ucdl.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.UcdlToken;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UcdlParserTest {

  String testDefinition =
      """
          %s:
            description: %s
            default_type: %s
            parameter: %s
            definition: %s
          """;

  @Test
  void getDefinitions() throws ParseException {
    assertDoesNotThrow(() -> UcdlParser.getDefinitions(""));
    HashMap<String, ConstraintDefinitionDto> definitions = UcdlParser.getDefinitions("");
    assertEquals(definitions.size(), 0);

    checkValidDefinition("name", "description", "", new ArrayList<>(), new ArrayList<>());
    checkValidDefinition("name", "description",
        """
            \n
                student: Student
                studentGroup: Student-Group
                teacher: Teacher
                room: Room
                tag: Tag
                subject: Subject
                grade: Grade
                timeslot: Timeslot""",
        List.of("student", "studentGroup", "teacher", "room", "tag", "subject", "grade",
            "timeslot"),
        List.of(ResourceType.STUDENT, ResourceType.STUDENT_GROUP, ResourceType.TEACHER,
            ResourceType.ROOM, ResourceType.TAG, ResourceType.SUBJECT, ResourceType.GRADE,
            ResourceType.TIMESLOT));


    String input = """
        test:
          description: test
          default_type: HARD_PENALIZE
          parameter:
          definition: true
        """;
    String finalInput = input;
    assertDoesNotThrow(() -> UcdlParser.getDefinitions(finalInput));
    definitions = UcdlParser.getDefinitions(input);
    assertEquals(definitions.size(), 1);
    assertTrue(definitions.containsKey("test"));
    assertEquals(definitions.get("test").defaultType(), RewardPenalize.HARD_PENALIZE);

    input = """
        test:
          description: test
          default_type: SOFT_PENALIZE
          parameter:
          definition: true
        """;
    String finalInput1 = input;
    assertDoesNotThrow(() -> UcdlParser.getDefinitions(finalInput1));
    definitions = UcdlParser.getDefinitions(input);
    assertEquals(definitions.size(), 1);
    assertTrue(definitions.containsKey("test"));
    assertEquals(definitions.get("test").defaultType(), RewardPenalize.SOFT_PENALIZE);

    input = """
        test:
          description: test
          default_type: HARD_REWARD
          parameter:
          definition: true
        """;
    String finalInput2 = input;
    assertDoesNotThrow(() -> UcdlParser.getDefinitions(finalInput2));
    definitions = UcdlParser.getDefinitions(input);
    assertEquals(definitions.size(), 1);
    assertTrue(definitions.containsKey("test"));
    assertEquals(definitions.get("test").defaultType(), RewardPenalize.HARD_REWARD);

    input = """
        test:
          description: test
          default_type: SOFT_REWARD
          parameter:
          definition: true
        """;
    String finalInput3 = input;
    assertDoesNotThrow(() -> UcdlParser.getDefinitions(finalInput3));
    definitions = UcdlParser.getDefinitions(input);
    assertEquals(definitions.size(), 1);
    assertTrue(definitions.containsKey("test"));
    assertEquals(definitions.get("test").defaultType(), RewardPenalize.SOFT_REWARD);


    checkInvalidDefinition("name", "description", "test", "", "true", ParseException.class);
    checkInvalidDefinition("name", "description", "", "test", "true", ParseException.class);
    checkInvalidDefinition("", "description", "", "", "true", ParseException.class);
    checkInvalidDefinition("name", "", "", "", "true", ParseException.class);
    checkInvalidDefinition("name", "description", "", "", "", ParseException.class);
    checkInvalidDefinition("name", "true", "", "", "true", ParseException.class);
    checkInvalidDefinition("name", "description", "", "", "test", ParseException.class);
    checkInvalidDefinition("name", "description", "", "\n    student: student", "true",
        ParseException.class);
    checkInvalidDefinition("name", "description", "", "\n    this: Student", "true",
        ParseException.class);
    checkInvalidDefinition("name", "description", "", "\n    student:", "true",
        ParseException.class);
    checkInvalidDefinition("name", "description", "", "\n    student: true", "true",
        ParseException.class);

    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("test"));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("test:"));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("test: test"));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("""
        test:
          A:
        """));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("""
        test:
          A:
          description:
        """));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("""
        test:
          A:
          description:
          default_type:
        """));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("""
        test:
          A:
          description:
          default_type:
          parameter:
        """));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("""
        test:
          A:
          description:
          default_type:
          parameter:
          definition:
        """));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("""
        test:
          description: " "
          default_type:
          parameter:
          definition:
        """));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("""
        test:
          description: test
          default_type: true
          parameter:
          definition:
        """));
    assertThrows(ParseException.class, () -> UcdlParser.getDefinitions("""
        test:
          description: test
          default_type:
          parameter:
          definition: 5
        """));
  }

  private void checkValidDefinition(String name, String description, String parameters,
                                    List<String> expectedParameterNames,
                                    List<ResourceType> expectedParameterTypes)
      throws ParseException {
    String input = testDefinition.formatted(name, description, "", parameters, "true");

    assertDoesNotThrow(() -> UcdlParser.getDefinitions(input));
    HashMap<String, ConstraintDefinitionDto> definitions = UcdlParser.getDefinitions(input);

    assertEquals(definitions.size(), 1);
    assertTrue(definitions.containsKey(name));
    ConstraintDefinitionDto definitionDto = definitions.get(name);
    assertEquals(definitionDto.name(), name);
    assertEquals(definitionDto.defaultType(), RewardPenalize.SOFT_PENALIZE);
    assertEquals(definitionDto.description(), description);
    assertEquals(definitionDto.root(), new ValueDto<>(UcdlToken.BOOL_VALUE, true));
    List<Map.Entry<String, ResourceType>> params =
        definitionDto.parameters().sequencedEntrySet().stream()
            .toList();


    for (int i = 0; i < params.size(); i++) {
      Map.Entry<String, ResourceType> entry = params.get(i);
      if (i == 0) {
        assertEquals(entry.getKey(), "this");
        assertEquals(entry.getValue(), ResourceType.TIMETABLE);
      } else {
        assertEquals(entry.getKey(), expectedParameterNames.get(i - 1));
        assertEquals(entry.getValue(), expectedParameterTypes.get(i - 1));
      }
    }
  }

  private void checkInvalidDefinition(String name, String description, String defaultType,
                                      String parameters, String definition,
                                      Class<? extends Exception> exceptionType) {
    String input = testDefinition.formatted(name, description, defaultType, parameters, definition);
    assertThrows(exceptionType, () -> UcdlParser.getDefinitions(input));
  }
}
