package de.uftos.timefold.constraints;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.UcdlToken;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.dto.ucdl.ast.ControlSequenceDto;
import de.uftos.dto.ucdl.ast.ElementDto;
import de.uftos.dto.ucdl.ast.OperatorDto;
import de.uftos.dto.ucdl.ast.QuantifierDto;
import de.uftos.dto.ucdl.ast.SetDto;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.timefold.domain.GradeTimefoldInstance;
import de.uftos.timefold.domain.LessonTimefoldInstance;
import de.uftos.timefold.domain.Number;
import de.uftos.timefold.domain.ResourceTimefoldInstance;
import de.uftos.timefold.domain.RoomTimefoldInstance;
import de.uftos.timefold.domain.StudentGroupTimefoldInstance;
import de.uftos.timefold.domain.StudentTimefoldInstance;
import de.uftos.timefold.domain.SubjectTimefoldInstance;
import de.uftos.timefold.domain.TagTimefoldInstance;
import de.uftos.timefold.domain.TeacherTimefoldInstance;
import de.uftos.timefold.domain.TimeslotTimefoldInstance;
import de.uftos.timefold.domain.TimetableSolutionTimefoldInstance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

//todo: update signatures to correct signature
public class ConstraintDefinitionFactory {

  public static ConstraintDefinitionTimefoldInstance getConstraintDefinition(
      ConstraintDefinitionDto definition) {
    String name = definition.name();
    RewardPenalize defaultType = definition.defaultType();
    Function<List<ResourceTimefoldInstance>, Boolean> function;

    LinkedHashMap<String, ResourceType> params = definition.parameters();

    AbstractSyntaxTreeDto ast = definition.root();

    if (ast.getToken() == UcdlToken.CODEBLOCK) {
      function = convertCodeblock(ast, params);
    } else {
      function = convertBool(ast, params);
    }

    return new ConstraintDefinitionTimefoldInstance(name, defaultType, function);
  }

  //The first parameter "List<ResourceTimefoldInstance" contains always the parameters given in the
  //constraint definition.

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertCodeblock(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.CODEBLOCK) {
      throw new IllegalStateException();
    }

    OperatorDto root = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Optional<Boolean>>> functions = new ArrayList<>();
    Function<List<ResourceTimefoldInstance>, Boolean> returnValue = null;

    for (AbstractSyntaxTreeDto child : root.parameters()) {
      if (child.getToken() == UcdlToken.FOR) {
        functions.add(convertFor(child, params));
      } else if (child.getToken() == UcdlToken.IF) {
        functions.add(convertIf(child, params));
      } else if (child.getToken() == UcdlToken.RETURN) {
        returnValue = convertReturn(child, params);
        break;
      } else {
        throw new IllegalStateException();
      }
    }

    if (returnValue == null) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Boolean> finalReturnValue = returnValue;
    return (parameters) -> {
      for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> function : functions) {
        Optional<Boolean> bool = function.apply(parameters);
        if (bool.isPresent()) {
          return bool.get();
        }
      }
      return finalReturnValue.apply(parameters);
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Optional<Boolean>> convertFor(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.FOR) {
      throw new IllegalStateException();
    }

    ControlSequenceDto root = (ControlSequenceDto) ast;

    OperatorDto of = (OperatorDto) root.parenthesesContent();

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(of, params);

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    List<Function<List<ResourceTimefoldInstance>, Optional<Boolean>>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : root.body()) {
      if (child.getToken() == UcdlToken.FOR) {
        functions.add(convertFor(child, params));
      } else if (child.getToken() == UcdlToken.IF) {
        functions.add(convertIf(child, params));
      } else {
        throw new IllegalStateException();
      }
    }

    params.remove(variableReference.value());

    return (parameters) -> {
      Optional<Boolean> current = Optional.empty();
      for (ResourceTimefoldInstance resource : variableDefinition.apply(parameters)) {
        for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> function : functions) {
          parameters.add(resource);
          current = function.apply(parameters);
          parameters.remove(resource);
          if (current.isPresent()) {
            return current;
          }
        }
      }
      return current;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Optional<Boolean>> convertIf(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.IF) {
      throw new IllegalStateException();
    }

    ControlSequenceDto root = (ControlSequenceDto) ast;

    Function<List<ResourceTimefoldInstance>, Boolean> condition =
        convertBool(root.parenthesesContent(), params);

    if (root.body().size() == 1 && root.body().getFirst().getToken() == UcdlToken.RETURN) {
      Function<List<ResourceTimefoldInstance>, Boolean> returnValue =
          convertReturn(root.body().getFirst(), params);

      return (parameters) -> {
        if (condition.apply(parameters)) {
          return Optional.of(returnValue.apply(parameters));
        }
        return Optional.empty();
      };
    }

    List<Function<List<ResourceTimefoldInstance>, Optional<Boolean>>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : root.body()) {
      if (child.getToken() == UcdlToken.FOR) {
        functions.add(convertFor(child, params));
      } else if (child.getToken() == UcdlToken.IF) {
        functions.add(convertIf(child, params));
      } else {
        throw new IllegalStateException();
      }
    }

    return (parameters) -> {
      Optional<Boolean> current = Optional.empty();
      if (condition.apply(parameters)) {
        for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> function : functions) {
          current = function.apply(parameters);
          if (current.isPresent()) {
            return current;
          }
        }
      }
      return current;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertReturn(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.RETURN) {
      throw new IllegalStateException();
    }
    boolean value = ((ValueDto<Boolean>) ast).value();
    return (parameters) -> value;
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertBool(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return switch (ast.getToken()) {
      case IMPLIES -> convertImplies(ast, params);
      case OR -> convertOr(ast, params);
      case AND -> convertAnd(ast, params);
      case NOT -> convertNot(ast, params);
      case FOR_ALL -> convertForAll(ast, params);
      case EXISTS -> convertExists(ast, params);
      case BOOL_VALUE -> convertBoolValue(ast, params);
      case IN -> convertIn(ast, params);
      case IS_EMPTY -> convertIsEmpty(ast, params);
      default -> convertEquation(ast, params);
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertAnd(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.AND) {
      throw new IllegalStateException();
    }

    OperatorDto and = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Boolean>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : and.parameters()) {
      functions.add(convertBool(child, params));
    }

    return (parameters) -> {
      boolean returnValue = true;
      for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
        returnValue = returnValue && function.apply(parameters);
      }
      return returnValue;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertOr(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.OR) {
      throw new IllegalStateException();
    }

    OperatorDto or = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Boolean>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : or.parameters()) {
      functions.add(convertBool(child, params));
    }

    return (parameters) -> {
      boolean returnValue = false;
      for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
        returnValue = returnValue || function.apply(parameters);
      }
      return returnValue;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertNot(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.NOT) {
      throw new IllegalStateException();
    }

    OperatorDto not = (OperatorDto) ast;

    if (not.parameters().size() != 1) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Boolean> bool =
        convertBool(not.parameters().getFirst(), params);

    return (parameters) -> !bool.apply(parameters);
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertImplies(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.IMPLIES) {
      throw new IllegalStateException();
    }

    OperatorDto implies = (OperatorDto) ast;

    if (implies.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Boolean> firstParameter =
        convertBool(implies.parameters().getFirst(), params);
    Function<List<ResourceTimefoldInstance>, Boolean> secondParameter =
        convertBool(implies.parameters().getLast(), params);

    return (parameters) -> !firstParameter.apply(parameters) || secondParameter.apply(parameters);
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertForAll(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.FOR_ALL) {
      throw new IllegalStateException();
    }

    QuantifierDto root = (QuantifierDto) ast;

    OperatorDto of = (OperatorDto) root.elements();

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(of, params);

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(root.body(), params);

    params.remove(variableReference.value());

    return (parameters) -> {
      boolean and = true;
      for (ResourceTimefoldInstance resource : variableDefinition.apply(parameters)) {
        parameters.add(resource);
        and = and && function.apply(parameters);
        parameters.remove(resource);
      }
      return and;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertExists(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.EXISTS) {
      throw new IllegalStateException();
    }

    QuantifierDto root = (QuantifierDto) ast;
    OperatorDto of = (OperatorDto) root.elements();

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(of, params);


    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(root.body(), params);

    params.remove(variableReference.value());

    return (parameters) -> {
      boolean or = false;
      for (ResourceTimefoldInstance resource : variableDefinition.apply(parameters)) {
        parameters.add(resource);
        or = or || function.apply(parameters);
        parameters.remove(resource);
      }
      return or;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertBoolValue(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.BOOL_VALUE) {
      throw new IllegalStateException();
    }
    boolean value = ((ValueDto<Boolean>) ast).value();
    return (parameters) -> value;
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertEquation(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return switch (ast.getToken()) {
      case EQUALS -> convertEquals(ast, params);
      case NOT_EQUALS -> convertNotEquals(ast, params);
      case SMALLER_EQUALS -> convertSmallerEquals(ast, params);
      case SMALLER -> convertSmaller(ast, params);
      case GREATER_EQUALS -> convertGreaterEquals(ast, params);
      case GREATER -> convertGreater(ast, params);
      default -> throw new IllegalStateException();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertEquals(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.EQUALS) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst(), params);
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast(), params);
    return (parameters) -> {
      ResourceTimefoldInstance first = firstElement.apply(parameters);
      ResourceTimefoldInstance second = secondElement.apply(parameters);

      if (first.getResourceType() != second.getResourceType()) {
        throw new IllegalArgumentException();
      }

      return first.equals(second);
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertNotEquals(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.NOT_EQUALS) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst(), params);
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast(), params);
    return (parameters) -> {
      ResourceTimefoldInstance first = firstElement.apply(parameters);
      ResourceTimefoldInstance second = secondElement.apply(parameters);

      if (first.getResourceType() != second.getResourceType()) {
        throw new IllegalArgumentException();
      }

      return !first.equals(second);
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertSmallerEquals(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.SMALLER_EQUALS) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst(), params);
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast(), params);
    return (parameters) -> {
      ResourceTimefoldInstance first = firstElement.apply(parameters);
      ResourceTimefoldInstance second = secondElement.apply(parameters);

      if (first.getResourceType() != ResourceType.NUMBER
          || second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((Number) first).getValue() <= ((Number) second).getValue();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertSmaller(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.SMALLER) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst(), params);
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast(), params);
    return (parameters) -> {
      ResourceTimefoldInstance first = firstElement.apply(parameters);
      ResourceTimefoldInstance second = secondElement.apply(parameters);

      if (first.getResourceType() != ResourceType.NUMBER
          || second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((Number) first).getValue() < ((Number) second).getValue();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertGreaterEquals(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.GREATER_EQUALS) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst(), params);
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast(), params);
    return (parameters) -> {
      ResourceTimefoldInstance first = firstElement.apply(parameters);
      ResourceTimefoldInstance second = secondElement.apply(parameters);

      if (first.getResourceType() != ResourceType.NUMBER
          || second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((Number) first).getValue() >= ((Number) second).getValue();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertGreater(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.GREATER) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst(), params);
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast(), params);
    return (parameters) -> {
      ResourceTimefoldInstance first = firstElement.apply(parameters);
      ResourceTimefoldInstance second = secondElement.apply(parameters);

      if (first.getResourceType() != ResourceType.NUMBER
          || second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((Number) first).getValue() > ((Number) second).getValue();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertIn(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.IN) {
      throw new IllegalStateException();
    }
    OperatorDto in = (OperatorDto) ast;
    if (in.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> element =
        convertElement(in.parameters().getFirst(), params);
    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> set =
        convertSet(in.parameters().getLast(), params);
    return (parameters) -> set.apply(parameters).contains(element.apply(parameters));
  }

  private static Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertOf(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.OF) {
      throw new IllegalStateException();
    }

    OperatorDto of = (OperatorDto) ast;

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    AbstractSyntaxTreeDto set = of.parameters().getLast();

    return convertSet(set, params);
  }

  //todo: finish implementing filters
  private static Function<List<ResourceTimefoldInstance>, Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>> convertFilter(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.FILTER) {
      throw new IllegalStateException();
    }

    OperatorDto filter = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Boolean>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto param : filter.parameters()) {
      if (param.getToken() == UcdlToken.NUMBER_SET || param.getToken() == UcdlToken.RESOURCE_SET) {
        functions.add(null);
      } else {
        functions.add(null);
      }
    }

    return null;
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertIsEmpty(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.IS_EMPTY) {
      throw new IllegalStateException();
    }
    OperatorDto isEmpty = (OperatorDto) ast;
    if (isEmpty.parameters().size() != 1) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> set =
        convertSet(isEmpty.parameters().getFirst(), params);
    return (parameters) -> set.apply(parameters).isEmpty();
  }

  private static Function<List<ResourceTimefoldInstance>, Number> convertSize(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.SIZE) {
      throw new IllegalStateException();
    }
    OperatorDto size = (OperatorDto) ast;
    if (size.parameters().size() != 1) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> set =
        convertSet(size.parameters().getFirst(), params);
    return (parameters) -> new Number(set.apply(parameters).size());
  }

  private static Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertSet(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return switch (ast.getToken()) {
      case RESOURCE_SET -> convertResourceSet(ast, params);
      case NUMBER_SET -> convertNumberSet(ast, params);
      default -> throw new IllegalStateException();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertResourceSet(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.RESOURCE_SET) {
      throw new IllegalStateException();
    }
    SetDto set = (SetDto) ast;

    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> name;

    if (set.setName().getToken() == UcdlToken.VALUE_REFERENCE) {
      name = convertValueReference(set.setName(), params);
    } else {
      name = convertElement(set.setName(), params);
    }

    List<Function<List<ResourceTimefoldInstance>, Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>>>
        modifiers = new ArrayList<>();

    for (AbstractSyntaxTreeDto modifier : set.modifiers()) {
      if (modifier.getToken() == UcdlToken.ATTRIBUTE) {
        modifiers.add(convertAttribute(modifier, params));
      } else {
        modifiers.add(convertFilter(modifier, params));
      }
    }

    return (parameters) -> {
      Set<ResourceTimefoldInstance> setReference = new HashSet<>();
      setReference.add(name.apply(parameters));

      for (Function<List<ResourceTimefoldInstance>, Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>> function : modifiers) {
        setReference = function.apply(parameters).apply(setReference);
      }
      return setReference;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertNumberSet(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.NUMBER_SET) {
      throw new IllegalStateException();
    }
    ValueDto<Integer[]> set = (ValueDto<Integer[]>) ast;

    Set<ResourceTimefoldInstance> resources = new HashSet<>();

    for (Integer i : set.value()) {
      resources.add(new Number(i));
    }

    return (parameters) -> resources;
  }

  //todo: replace if-else with switch
  private static Function<List<ResourceTimefoldInstance>, Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>> convertAttribute(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.ATTRIBUTE) {
      throw new IllegalStateException();
    }

    String attribute = ((ValueDto<String>) ast).value();

    return switch (attribute) {
      case "index" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(new Number(((LessonTimefoldInstance) resource).getIndex()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "teacher" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getTeacher());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "timeslot" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getTimeslot());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "room" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getRoom());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "subject" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getSubject());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "studentGroup" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getStudentGroup());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "grade" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.STUDENT_GROUP) {
            resultSet.add(((StudentGroupTimefoldInstance) resource).getGrade());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "day" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMESLOT) {
            resultSet.add(new Number(((TimeslotTimefoldInstance) resource).getDayOfWeek()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "slot" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMESLOT) {
            resultSet.add(new Number(((TimeslotTimefoldInstance) resource).getSlotId()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "students" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getStudents());
          } else if (resource.getResourceType() == ResourceType.TAG) {
            resultSet.addAll(((TagTimefoldInstance) resource).getStudentList());
          } else if (resource.getResourceType() == ResourceType.STUDENT_GROUP) {
            resultSet.addAll(((StudentGroupTimefoldInstance) resource).getStudentList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "teachers" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getTeachers());
          } else if (resource.getResourceType() == ResourceType.TAG) {
            resultSet.addAll(((TagTimefoldInstance) resource).getTeacherList());
          } else if (resource.getResourceType() == ResourceType.SUBJECT) {
            resultSet.addAll(((SubjectTimefoldInstance) resource).getTeacherList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "studentGroups" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getStudentGroups());
          } else if (resource.getResourceType() == ResourceType.TAG) {
            resultSet.addAll(((TagTimefoldInstance) resource).getStudentGroupList());
          } else if (resource.getResourceType() == ResourceType.GRADE) {
            resultSet.addAll(((GradeTimefoldInstance) resource).getStudentGroupList());
          } else if (resource.getResourceType() == ResourceType.STUDENT) {
            resultSet.addAll(((StudentTimefoldInstance) resource).getStudentGroupList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "rooms" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getRooms());
          } else if (resource.getResourceType() == ResourceType.TAG) {
            resultSet.addAll(((TagTimefoldInstance) resource).getRoomList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "subjects" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getSubjects());
          } else if (resource.getResourceType() == ResourceType.TAG) {
            resultSet.addAll(((TagTimefoldInstance) resource).getSubjectList());
          } else if (resource.getResourceType() == ResourceType.TEACHER) {
            resultSet.addAll(((TeacherTimefoldInstance) resource).getSubjectList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "grades" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getGrades());
          } else if (resource.getResourceType() == ResourceType.TAG) {
            resultSet.addAll(((TagTimefoldInstance) resource).getGradeList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "timeslots" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getTimeslots());
          } else if (resource.getResourceType() == ResourceType.TAG) {
            resultSet.addAll(((TagTimefoldInstance) resource).getTimeslotList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "lessons" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getLessons());
          } else if (resource.getResourceType() == ResourceType.TEACHER) {
            resultSet.addAll(((TeacherTimefoldInstance) resource).getLessonList());
          } else if (resource.getResourceType() == ResourceType.STUDENT_GROUP) {
            resultSet.addAll(((StudentGroupTimefoldInstance) resource).getLessonList());
          } else if (resource.getResourceType() == ResourceType.ROOM) {
            resultSet.addAll(((RoomTimefoldInstance) resource).getLessonList());
          } else if (resource.getResourceType() == ResourceType.SUBJECT) {
            resultSet.addAll(((SubjectTimefoldInstance) resource).getLessonList());
          } else if (resource.getResourceType() == ResourceType.TIMESLOT) {
            resultSet.addAll(((TimeslotTimefoldInstance) resource).getLessonList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "tags" -> (parameters) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMETABLE) {
            resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getTags());
          } else if (resource.getResourceType() == ResourceType.STUDENT) {
            resultSet.addAll(((StudentTimefoldInstance) resource).getProvidedTagsList());
          } else if (resource.getResourceType() == ResourceType.TEACHER) {
            resultSet.addAll(((TeacherTimefoldInstance) resource).getProvidedTagsList());
          } else if (resource.getResourceType() == ResourceType.STUDENT_GROUP) {
            resultSet.addAll(((StudentGroupTimefoldInstance) resource).getProvidedTagsList());
          } else if (resource.getResourceType() == ResourceType.ROOM) {
            resultSet.addAll(((RoomTimefoldInstance) resource).getProvidedTagsList());
          } else if (resource.getResourceType() == ResourceType.SUBJECT) {
            resultSet.addAll(((SubjectTimefoldInstance) resource).getProvidedTagsList());
          } else if (resource.getResourceType() == ResourceType.GRADE) {
            resultSet.addAll(((GradeTimefoldInstance) resource).getProvidedTagsList());
          } else if (resource.getResourceType() == ResourceType.TIMESLOT) {
            resultSet.addAll(((TimeslotTimefoldInstance) resource).getProvidedTagsList());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      default -> throw new IllegalStateException();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> convertElement(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() == UcdlToken.NUMBER || ast.getToken() == UcdlToken.SIZE) {
      Function<List<ResourceTimefoldInstance>, Number> function = convertNumberElement(ast, params);
      return (parameters) -> (ResourceTimefoldInstance) function.apply(parameters);
    }

    if (ast.getToken() != UcdlToken.ELEMENT) {
      throw new IllegalStateException();
    }
    ElementDto element = (ElementDto) ast;

    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> name =
        convertValueReference(element.name(), params);

    List<Function<List<ResourceTimefoldInstance>, Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>>>
        attributes = new ArrayList<>();

    for (AbstractSyntaxTreeDto attribute : element.attributes()) {
      attributes.add(convertAttribute(attribute, params));
    }

    return (parameters) -> {
      Set<ResourceTimefoldInstance> elementReference = new HashSet<>();
      elementReference.add(name.apply(parameters));

      for (Function<List<ResourceTimefoldInstance>, Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>> function : attributes) {
        elementReference = function.apply(parameters).apply(elementReference);
      }
      if (elementReference.size() != 1) {
        throw new IllegalStateException();
      }
      return elementReference.iterator().next();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Number> convertNumberElement(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return switch (ast.getToken()) {
      case NUMBER -> convertNumber(ast, params);
      case SIZE -> convertSize(ast, params);
      default -> throw new IllegalStateException();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Number> convertNumber(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.NUMBER) {
      throw new IllegalStateException();
    }
    ValueDto<Integer> value = (ValueDto<Integer>) ast;
    return (parameters) -> new Number(value.value());
  }

  private static Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> convertValueReference(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.VALUE_REFERENCE) {
      throw new IllegalStateException();
    }
    ValueDto<String> reference = (ValueDto<String>) ast;
    //searching index of parameter in HashMap which corresponds to the index of the parameter in the
    //parameter list given to the returned function
    Iterator<String> iterator = params.sequencedKeySet().iterator();
    int index = 0;
    while (iterator.hasNext()) {
      if (iterator.next().equals(reference.value())) {
        break;
      }
      index++;
    }
    int finalIndex = index;
    ResourceType type = params.get(reference.value());
    return (parameters) -> {
      ResourceTimefoldInstance resource = parameters.get(finalIndex);
      if (resource.getResourceType() != type) {
        throw new IllegalArgumentException();
      }
      return resource;
    };
  }

  /*
  //control sequences
  CODEBLOCK, //-> operatordto       x
  FOR, //-> controlsequencedto      x
  IF, //-> controlsequencedto       x
  RETURN, //-> valuedto             x

  //bool
  AND, //-> operatordto             x
  OR, //-> operatordto              x
  NOT, //-> operatordto             x
  IMPLIES, //-> operatordto         x
  FOR_ALL, //-> quantifierdto       x
  EXISTS, //-> quantifierdto        x
  BOOL_VALUE, //-> valuedto         x

  //comparisons
  EQUALS, //-> operatordto          x
  NOT_EQUALS, //-> operatordto      x
  SMALLER_EQUALS, //-> operatordto  x
  GREATER_EQUALS, //-> operatordto  x
  SMALLER, //-> operatordto         x
  GREATER, //-> operatordto         x

  //sets and elements
  IN, //-> operatordto              x
  OF, //-> operatordto              x
  FILTER, //-> operatordto          -
  IS_EMPTY, //-> operatordto        x
  SIZE, //-> operatordto            x
  RESOURCE_SET, //-> valuedto       x
  NUMBER_SET, //-> valuedto         x
  ATTRIBUTE, //-> valuedto          x
  ELEMENT, //-> valuedto            x
  NUMBER, //-> valuedto             x
  VALUE_REFERENCE //-> valuedto     x
   */
}
