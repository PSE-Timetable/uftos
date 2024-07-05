package de.uftos.repositories.ucdl.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.solver.RewardPenalize;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

public class UcdlParser {
  private static final YAMLMapper MAPPER = new YAMLMapper();

  public static HashMap<String, ConstraintDefinitionDto> getDefinitions(String input)
      throws ParseException, IOException {
    HashMap<String, ConstraintDefinitionDto> constraints = new HashMap<>();

    JsonNode jsonNode = MAPPER.readTree(input);

    jsonNode.fields().forEachRemaining((entry) -> {
      try {
        constraints.put(entry.getKey(), parseConstraintDefinition(entry.getKey(), entry.getValue()));
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    });

    System.out.println("-------------------------");

    return constraints;
  }

  private static ConstraintDefinitionDto parseConstraintDefinition(String name, JsonNode constraintDefinition)
      throws ParseException {
    constraintDefinition.fieldNames().forEachRemaining(System.out::println);
    String description = constraintDefinition.get("description").textValue();
    RewardPenalize defaultType = switch (constraintDefinition.get("default_type").textValue())
    {
      case "HARD_REWARD" -> RewardPenalize.HARD_REWARD;
      case "SOFT_REWARD" -> RewardPenalize.SOFT_REWARD;
      case "HARD_PENALIZE" -> RewardPenalize.HARD_PENALIZE;
      case "SOFT_PENALIZE" -> RewardPenalize.SOFT_PENALIZE;
      case null -> RewardPenalize.SOFT_PENALIZE;
      default -> throw new ParseException("Invalid default_type. \"HARD_REWARD\", \"SOFT_REWARD\", \"HARD_PENALIZE\", \"SOFT_PENALIZE\", or empty field expected", 0);
    };
    HashMap<String, ResourceType> parameters = new HashMap<>();
    constraintDefinition.get("parameter").fields().forEachRemaining((entry) -> {
      if (entry != null) {
        parameters.put(entry.getKey(), getResourceType(entry.getValue().textValue()));
      }
    });
    AbstractSyntaxTreeDto definition = parseDefinition(constraintDefinition.get("definition").textValue());

    return new ConstraintDefinitionDto(name, description, defaultType, parameters, definition);
  }

  private static AbstractSyntaxTreeDto parseDefinition(String definition) {
    AbstractSyntaxTreeDto parsedDefinition = DefinitionParser.parseDefinition(definition);
    System.out.println(parsedDefinition);
    return parsedDefinition;
  }

  private static ResourceType getResourceType(String resourceType) {
    return switch (resourceType) {
      case "Student" -> ResourceType.STUDENT;
      case "Student-Group" -> ResourceType.STUDENT_GROUP;
      case "Teacher" -> ResourceType.TEACHER;
      case "Room" -> ResourceType.ROOM;
      case "Tag" -> ResourceType.TAG;
      case "Subject" -> ResourceType.SUBJECT;
      case "Grade" -> ResourceType.GRADE;
      case "Lesson" -> ResourceType.LESSON;
      case "Timeslot" -> ResourceType.TIMESLOT;
      default -> throw new IllegalArgumentException();
    };
  }

}
