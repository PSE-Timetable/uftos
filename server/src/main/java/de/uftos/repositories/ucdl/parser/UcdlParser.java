package de.uftos.repositories.ucdl.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UcdlParser {
  private static final YAMLMapper MAPPER = new YAMLMapper();

  public static HashMap<String, ConstraintDefinitionDto> getDefinitions(String input)
      throws JsonProcessingException, ParseException {
    HashMap<String, ConstraintDefinitionDto> constraints = new HashMap<>();

    JsonNode jsonNode = MAPPER.readTree(input);

    Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> entry = iterator.next();
      constraints.put(entry.getKey(),
          parseConstraintDefinition(entry.getKey(), entry.getValue()));
    }
    return constraints;
  }

  private static ConstraintDefinitionDto parseConstraintDefinition(String name,
                                                                   JsonNode constraintDefinition)
      throws ParseException {
    if (constraintDefinition.get("description") == null) {
      throw new ParseException("Constraint description of constraint " + name + " is missing!");
    }
    if (constraintDefinition.get("default_type") == null) {
      throw new ParseException("Default type of constraint " + name + " is missing!");
    }
    if (constraintDefinition.get("parameter") == null) {
      throw new ParseException("Parameters of constraint " + name + " are missing!");
    }
    if (constraintDefinition.get("definition") == null) {
      throw new ParseException("Definition of constraint " + name + " is missing!");
    }

    String description = constraintDefinition.get("description").textValue();

    RewardPenalize defaultType = switch (constraintDefinition.get("default_type").textValue()) {
      case "HARD_REWARD" -> RewardPenalize.HARD_REWARD;
      case "SOFT_REWARD" -> RewardPenalize.SOFT_REWARD;
      case "HARD_PENALIZE" -> RewardPenalize.HARD_PENALIZE;
      case "SOFT_PENALIZE" -> RewardPenalize.SOFT_PENALIZE;
      case null -> RewardPenalize.SOFT_PENALIZE;
      default -> throw new ParseException(
          "Invalid default_type. \"HARD_REWARD\", \"SOFT_REWARD\", \"HARD_PENALIZE\", \"SOFT_PENALIZE\", or empty field expected!");
    };

    HashMap<String, ResourceType> parameters = new HashMap<>();
    constraintDefinition.get("parameter").fields().forEachRemaining((entry) -> {
      if (entry != null) {
        try {
          parameters.put(entry.getKey(), getResourceType(entry.getValue().textValue()));
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
    });
    AbstractSyntaxTreeDto definition =
        parseDefinition(constraintDefinition.get("definition").textValue(), parameters);

    return new ConstraintDefinitionDto(name, description, defaultType, parameters, definition);
  }

  private static AbstractSyntaxTreeDto parseDefinition(String definition,
                                                       HashMap<String, ResourceType> parameters)
      throws de.uftos.repositories.ucdl.parser.javacc.ParseException {
    return DefinitionParser.parseDefinition(definition, parameters);
  }

  private static ResourceType getResourceType(String resourceType) throws ParseException {
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
      default -> throw new ParseException("Illegal resource type \"" + resourceType +
          "! Expected one of \"Student\", \"Student-Group\", \"Teacher\", \"Room\", \"Tag\", \"Subject\", \"Grade\", \"Timeslot\", \"Lesson\"!");
    };
  }

}
