package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import org.yaml.snakeyaml.parser.ParserException;

/**
 * This class parses ucdl-code and returns the encoded constraint definitions.
 */
public class UcdlParser {
  /**
   * Parses the input ucdl-String and returns the ConstraintDefinitions resembling the given code.
   *
   * @param input the given ucdl String
   * @return a Hashmap of all given ConstraintDefinitions (addressable using the constraint name).
   * @throws ParseException if the ucdl code in the "definition"-fields
   *                        of the constraints isn't correct.
   */
  public static HashMap<String, ConstraintDefinitionDto> getDefinitions(String input)
      throws ParseException {
    HashMap<String, ConstraintDefinitionDto> constraints = new HashMap<>();

    LoaderOptions loaderOptions = new LoaderOptions();
    loaderOptions.setAllowDuplicateKeys(false);

    Yaml yaml = new Yaml(loaderOptions);
    try {
      Object yamlCode = yaml.load(input);
      if (yamlCode == null) {
        return new HashMap<>();
      }
      if (!yamlCode.getClass().equals(LinkedHashMap.class)) {
        throw new ParseException("This is illegal code!");
      }
      HashMap<String, Object> constraintDefinitions = new HashMap<>();
      for (Map.Entry<?, ?> entry : ((LinkedHashMap<?, ?>) yamlCode).entrySet()) {
        constraintDefinitions.put(entry.getKey().toString(), entry.getValue());
      }
      for (Map.Entry<String, Object> entry : constraintDefinitions.entrySet()) {
        String name = entry.getKey();
        if (entry.getValue() == null || !entry.getValue().getClass().equals(LinkedHashMap.class)) {
          throw new ParseException(
              "Constraint " + name + " doesn't have proper values assigned!");
        }
        constraints.put(name,
            parseConstraintDefinition(name, (HashMap<String, Object>) entry.getValue()));
      }
    } catch (DuplicateKeyException | ParserException e) {
      throw new ParseException(e.getMessage());
    }
    return constraints;
  }

  //parses one full definition
  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  private static ConstraintDefinitionDto parseConstraintDefinition(String name,
                                                                   HashMap<String, Object> constraintDefinition)
      throws ParseException {
    if (!constraintDefinition.containsKey("description")) {
      throw new ParseException("Constraint " + name + " is missing field \"description\"!");
    }
    if (!constraintDefinition.containsKey("default_type")) {
      throw new ParseException("Constraint " + name + " is missing field \"default_type\"!");
    }
    if (!constraintDefinition.containsKey("parameter")) {
      throw new ParseException("Constraint " + name + " is missing field \"parameter\"!");
    }
    if (!constraintDefinition.containsKey("definition")) {
      throw new ParseException("Constraint " + name + " is missing field \"definition\"!");
    }
    if (constraintDefinition.size() != 4) {
      throw new ParseException(
          "Constraints require exactly the fields \"description\", \"default_type\", \"parameter\" and \"definition\""
      );
    }


    Object descriptionObject = constraintDefinition.get("description");

    if (descriptionObject == null || !descriptionObject.getClass().equals(String.class)) {
      throw new ParseException("The description needs to contain a String!");
    }

    String description = (String) descriptionObject;

    if (description.isBlank()) {
      throw new ParseException("The description requires a non-whitespace text!");
    }


    Object defaultTypeObject = constraintDefinition.get("default_type");

    if (defaultTypeObject != null && !defaultTypeObject.getClass().equals(String.class)) {
      throw new ParseException(
          "Invalid default_type. \"HARD_REWARD\", \"SOFT_REWARD\", \"HARD_PENALIZE\","
              + " \"SOFT_PENALIZE\", or empty field expected!");
    }

    RewardPenalize defaultType = switch ((String) defaultTypeObject) {
      case "HARD_REWARD" -> RewardPenalize.HARD_REWARD;
      case "SOFT_REWARD" -> RewardPenalize.SOFT_REWARD;
      case "HARD_PENALIZE" -> RewardPenalize.HARD_PENALIZE;
      case "SOFT_PENALIZE" -> RewardPenalize.SOFT_PENALIZE;
      case null -> RewardPenalize.SOFT_PENALIZE;
      default -> throw new ParseException(
          "Invalid default_type. \"HARD_REWARD\", \"SOFT_REWARD\", \"HARD_PENALIZE\","
              + " \"SOFT_PENALIZE\", or empty field expected!");
    };


    Object parameterObject = constraintDefinition.get("parameter");

    if (parameterObject != null && !parameterObject.getClass().equals(LinkedHashMap.class)) {
      throw new ParseException("Parameter declaration in constraint " + name + " is invalid!");
    }

    LinkedHashMap<String, Object> parameterDefinitions =
        (LinkedHashMap<String, Object>) parameterObject;

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);

    if (parameterDefinitions != null) {
      for (Map.Entry<String, Object> entry : parameterDefinitions.sequencedEntrySet()) {
        String parameterName = entry.getKey();
        if (parameterName.equals("this")) {
          throw new ParseException("Parameters can't be called \"this\"!");
        }
        if (entry.getValue() == null) {
          throw new ParseException(
              "Parameter " + parameterName + " requires a type!");
        }
        if (!entry.getValue().getClass().equals(String.class)) {
          throw new ParseException(
              "The declaration of parameter " + parameterName + " is invalid!");
        }
        String parameterType = (String) entry.getValue();
        parameters.put(parameterName, getResourceType(parameterType));
      }
    }

    Object definitionObject = constraintDefinition.get("definition");
    String definitionString;

    if (definitionObject == null) {
      throw new ParseException("The definition requires code!");
    } else if (definitionObject.getClass().equals(String.class)) {
      definitionString = (String) definitionObject;
    } else if (definitionObject.getClass().equals(Boolean.class)) {
      definitionString = ((Boolean) definitionObject).toString();
    } else {
      throw new ParseException("The definition requires code!");
    }


    AbstractSyntaxTreeDto definition =
        DefinitionParser.parseDefinition(definitionString,
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
      case "Timeslot" -> ResourceType.TIMESLOT;
      default -> throw new ParseException("Illegal resource type \"" + resourceType
          + "! Expected one of \"Student\", \"Student-Group\", \"Teacher\", \"Room\", \"Tag\","
          + " \"Subject\", \"Grade\", \"Timeslot\"!");
    };
  }

}
