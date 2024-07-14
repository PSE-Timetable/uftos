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

/**
 * This class parses ucdl-code and returns the encoded constraint definitions.
 */
public class UcdlParser {
  private static final YAMLMapper MAPPER = new YAMLMapper();

  /**
   * Parses the input ucdl-String and returns the ConstraintDefinitions resembling the given code.
   *
   * @param input the given ucdl String
   * @return a Hashmap of all given ConstraintDefinitions (addressable using the constraint name).
   * @throws JsonProcessingException if the yml structure of the input String isn't correct.
   * @throws ParseException          if the ucdl code in the "definition"-fields
   *                                 of the constraints isn't correct.
   */
  public static HashMap<String, ConstraintDefinitionDto> getDefinitions(String input)
      throws JsonProcessingException, ParseException {
    HashMap<String, ConstraintDefinitionDto> constraints = new HashMap<>();

    JsonNode jsonNode = MAPPER.readTree(input);

    Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> entry = iterator.next();
      try {
        constraints.put(entry.getKey(),
            parseConstraintDefinition(entry.getKey(), entry.getValue()));
      } catch (ParseException e) {
        throw new ParseException(
            "Error in constraint definition \"" + entry.getKey() + "\":" + e.getMessage());
      }
    }
    return constraints;
  }

  //parses one full definition
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
          "Invalid default_type. \"HARD_REWARD\", \"SOFT_REWARD\", \"HARD_PENALIZE\","
              + " \"SOFT_PENALIZE\", or empty field expected!");
    };

    HashMap<String, ResourceType> parameters = new HashMap<>();

    Iterator<Map.Entry<String, JsonNode>> iterator = constraintDefinition.get("parameter").fields();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> entry = iterator.next();
      if (entry != null) {
        parameters.put(entry.getKey(), getResourceType(entry.getValue().textValue()));
      }
    }

    AbstractSyntaxTreeDto definition =
        DefinitionParser.parseDefinition(constraintDefinition.get("definition").textValue(),
            parameters);

    return new ConstraintDefinitionDto(name, description, defaultType, parameters, definition);
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
      default -> throw new ParseException("Illegal resource type \"" + resourceType
          + "! Expected one of \"Student\", \"Student-Group\", \"Teacher\", \"Room\", \"Tag\","
          + " \"Subject\", \"Grade\", \"Timeslot\", \"Lesson\"!");
    };
  }

}
