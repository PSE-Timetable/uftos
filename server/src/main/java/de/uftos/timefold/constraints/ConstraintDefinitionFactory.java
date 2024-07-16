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

  private static LinkedHashMap<String, ResourceType> params = null;

  public static ConstraintDefinitionTimefoldInstance getConstraintDefinition(
      ConstraintDefinitionDto definition) {
    String name = definition.name();
    RewardPenalize defaultType = definition.defaultType();
    Function<List<ResourceTimefoldInstance>, Boolean> function;

    params = definition.parameters();

    AbstractSyntaxTreeDto ast = definition.root();

    if (ast.getToken() == UcdlToken.CODEBLOCK) {
      function = convertCodeblock(ast);
    } else {
      function = convertBool(ast);
    }

    return new ConstraintDefinitionTimefoldInstance(name, defaultType, function);
  }

  //If not specified differently, then the first parameter "List<ResourceTimefoldInstance" are the
  //parameters given in the constraint definition.

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertCodeblock(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.CODEBLOCK) {
      throw new IllegalStateException();
    }

    OperatorDto root = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Optional<Boolean>>> functions = new ArrayList<>();
    Function<List<ResourceTimefoldInstance>, Boolean> returnValue = null;

    for (AbstractSyntaxTreeDto child : root.parameters()) {
      if (child.getToken() == UcdlToken.FOR) {
        functions.add(convertFor(child));
      } else if (child.getToken() == UcdlToken.IF) {
        functions.add(convertIf(child));
      } else if (child.getToken() == UcdlToken.RETURN) {
        returnValue = convertReturn(child);
        break;
      } else {
        throw new IllegalStateException();
      }
    }

    if (returnValue == null) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Boolean> finalReturnValue = returnValue;
    return (l) -> {
      for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> f : functions) {
        Optional<Boolean> bool = f.apply(l);
        if (bool.isPresent()) {
          return bool.get();
        }
      }
      return finalReturnValue.apply(l);
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Optional<Boolean>> convertFor(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.FOR) {
      throw new IllegalStateException();
    }

    ControlSequenceDto root = (ControlSequenceDto) ast;

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(root.parenthesesContent());

    OperatorDto of = (OperatorDto) root.parenthesesContent();

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    List<Function<List<ResourceTimefoldInstance>, Optional<Boolean>>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : root.body()) {
      if (child.getToken() == UcdlToken.FOR) {
        functions.add(convertFor(child));
      } else if (child.getToken() == UcdlToken.IF) {
        functions.add(convertIf(child));
      } else {
        throw new IllegalStateException();
      }
    }

    params.remove(variableReference.value());

    return (l) -> {
      Optional<Boolean> current = Optional.empty();
      for (ResourceTimefoldInstance resource : variableDefinition.apply(l)) {
        for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> f : functions) {
          l.add(resource);
          current = f.apply(l);
          l.remove(resource);
          if (current.isPresent()) {
            return current;
          }
        }
      }
      return current;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Optional<Boolean>> convertIf(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.IF) {
      throw new IllegalStateException();
    }

    ControlSequenceDto root = (ControlSequenceDto) ast;

    Function<List<ResourceTimefoldInstance>, Boolean> condition =
        convertBool(root.parenthesesContent());

    if (root.body().size() == 1 && root.body().getFirst().getToken() == UcdlToken.RETURN) {
      Function<List<ResourceTimefoldInstance>, Boolean> returnValue =
          convertReturn(root.body().getFirst());

      return (l) -> {
        if (condition.apply(l)) {
          return Optional.of(returnValue.apply(l));
        }
        return Optional.empty();
      };
    }

    List<Function<List<ResourceTimefoldInstance>, Optional<Boolean>>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : root.body()) {
      if (child.getToken() == UcdlToken.FOR) {
        functions.add(convertFor(child));
      } else if (child.getToken() == UcdlToken.IF) {
        functions.add(convertIf(child));
      } else {
        throw new IllegalStateException();
      }
    }

    return (l) -> {
      Optional<Boolean> current = Optional.empty();
      if (condition.apply(l)) {
        for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> f : functions) {
          current = f.apply(l);
          if (current.isPresent()) {
            return current;
          }
        }
      }
      return current;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertReturn(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.RETURN) {
      throw new IllegalStateException();
    }
    boolean value = ((ValueDto<Boolean>) ast).value();
    return (l) -> value;
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertBool(
      AbstractSyntaxTreeDto ast) {
    return switch (ast.getToken()) {
      case IMPLIES -> convertImplies(ast);
      case OR -> convertOr(ast);
      case AND -> convertAnd(ast);
      case NOT -> convertNot(ast);
      case FOR_ALL -> convertForAll(ast);
      case EXISTS -> convertExists(ast);
      case BOOL_VALUE -> convertBoolValue(ast);
      case IN -> convertIn(ast);
      case IS_EMPTY -> convertIsEmpty(ast);
      default -> convertEquation(ast);
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertAnd(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.AND) {
      throw new IllegalStateException();
    }

    OperatorDto and = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Boolean>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : and.parameters()) {
      functions.add(convertBool(child));
    }

    return (l) -> {
      boolean returnValue = true;
      for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
        returnValue = returnValue && function.apply(l);
      }
      return returnValue;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertOr(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.OR) {
      throw new IllegalStateException();
    }

    OperatorDto or = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Boolean>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : or.parameters()) {
      functions.add(convertBool(child));
    }

    return (l) -> {
      boolean returnValue = false;
      for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
        returnValue = returnValue || function.apply(l);
      }
      return returnValue;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertNot(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.NOT) {
      throw new IllegalStateException();
    }

    OperatorDto not = (OperatorDto) ast;

    if (not.parameters().size() != 1) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Boolean> bool =
        convertBool(not.parameters().getFirst());

    return (l) -> !bool.apply(l);
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertImplies(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.IMPLIES) {
      throw new IllegalStateException();
    }

    OperatorDto implies = (OperatorDto) ast;

    if (implies.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    Function<List<ResourceTimefoldInstance>, Boolean> firstParameter =
        convertBool(implies.parameters().getFirst());
    Function<List<ResourceTimefoldInstance>, Boolean> secondParameter =
        convertBool(implies.parameters().getLast());

    return (l) -> !firstParameter.apply(l) || secondParameter.apply(l);
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertForAll(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.FOR_ALL) {
      throw new IllegalStateException();
    }

    QuantifierDto root = (QuantifierDto) ast;

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(root.elements());

    OperatorDto of = (OperatorDto) root.elements();

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(root.body());

    params.remove(variableReference.value());

    return (l) -> {
      boolean and = true;
      for (ResourceTimefoldInstance resource : variableDefinition.apply(l)) {
        l.add(resource);
        and = and && function.apply(l);
        l.remove(resource);
      }
      return and;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertExists(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.EXISTS) {
      throw new IllegalStateException();
    }

    QuantifierDto root = (QuantifierDto) ast;

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(root.elements());

    OperatorDto of = (OperatorDto) root.elements();

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(root.body());

    params.remove(variableReference.value());

    return (l) -> {
      boolean or = false;
      for (ResourceTimefoldInstance resource : variableDefinition.apply(l)) {
        l.add(resource);
        or = or || function.apply(l);
        l.remove(resource);
      }
      return or;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertBoolValue(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.BOOL_VALUE) {
      throw new IllegalStateException();
    }
    boolean value = ((ValueDto<Boolean>) ast).value();
    return (l) -> value;
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertEquation(
      AbstractSyntaxTreeDto ast) {
    return switch (ast.getToken()) {
      case EQUALS -> convertEquals(ast);
      case NOT_EQUALS -> convertNotEquals(ast);
      case SMALLER_EQUALS -> convertSmallerEquals(ast);
      case SMALLER -> convertSmaller(ast);
      case GREATER_EQUALS -> convertGreaterEquals(ast);
      case GREATER -> convertGreater(ast);
      default -> throw new IllegalStateException();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertEquals(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.EQUALS) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst());
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast());
    return (l) -> firstElement.apply(l).equals(secondElement.apply(l));
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertNotEquals(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.NOT_EQUALS) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst());
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast());
    return (l) -> !firstElement.apply(l).equals(secondElement.apply(l));
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertSmallerEquals(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.SMALLER_EQUALS) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst());
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast());
    return (l) -> {
      ResourceTimefoldInstance first = firstElement.apply(l);
      ResourceTimefoldInstance second = secondElement.apply(l);

      if (first.getResourceType() != ResourceType.NUMBER ||
          second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((Number) first).getValue() <= ((Number) second).getValue();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertSmaller(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.SMALLER) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst());
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast());
    return (l) -> {
      ResourceTimefoldInstance first = firstElement.apply(l);
      ResourceTimefoldInstance second = secondElement.apply(l);

      if (first.getResourceType() != ResourceType.NUMBER ||
          second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((Number) first).getValue() < ((Number) second).getValue();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertGreaterEquals(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.GREATER_EQUALS) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst());
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast());
    return (l) -> {
      ResourceTimefoldInstance first = firstElement.apply(l);
      ResourceTimefoldInstance second = secondElement.apply(l);

      if (first.getResourceType() != ResourceType.NUMBER ||
          second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((Number) first).getValue() >= ((Number) second).getValue();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertGreater(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.GREATER) {
      throw new IllegalStateException();
    }
    OperatorDto equation = (OperatorDto) ast;
    if (equation.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> firstElement =
        convertElement(equation.parameters().getFirst());
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> secondElement =
        convertElement(equation.parameters().getLast());
    return (l) -> {
      ResourceTimefoldInstance first = firstElement.apply(l);
      ResourceTimefoldInstance second = secondElement.apply(l);

      if (first.getResourceType() != ResourceType.NUMBER ||
          second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((Number) first).getValue() > ((Number) second).getValue();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertIn(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.IN) {
      throw new IllegalStateException();
    }
    OperatorDto in = (OperatorDto) ast;
    if (in.parameters().size() != 2) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> element =
        convertElement(in.parameters().getFirst());
    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> set =
        convertSet(in.parameters().getLast());
    return (l) -> set.apply(l).contains(element.apply(l));
  }

  private static Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertOf(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.OF) {
      throw new IllegalStateException();
    }

    OperatorDto of = (OperatorDto) ast;

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    AbstractSyntaxTreeDto set = of.parameters().getLast();

    return convertSet(set);
  }

  //todo: fix filters
  private static Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertFilter(
      AbstractSyntaxTreeDto ast) {
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
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.IS_EMPTY) {
      throw new IllegalStateException();
    }
    OperatorDto isEmpty = (OperatorDto) ast;
    if (isEmpty.parameters().size() != 1) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> set =
        convertSet(isEmpty.parameters().getFirst());
    return (l) -> set.apply(l).isEmpty();
  }

  private static Function<List<ResourceTimefoldInstance>, Number> convertSize(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.SIZE) {
      throw new IllegalStateException();
    }
    OperatorDto size = (OperatorDto) ast;
    if (size.parameters().size() != 1) {
      throw new IllegalStateException();
    }
    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> set =
        convertSet(size.parameters().getFirst());
    return (l) -> new Number(set.apply(l).size());
  }

  private static Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertSet(
      AbstractSyntaxTreeDto ast) {
    return switch (ast.getToken()) {
      case RESOURCE_SET -> convertResourceSet(ast);
      case NUMBER_SET -> convertNumberSet(ast);
      default -> throw new IllegalStateException();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertResourceSet(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.RESOURCE_SET) {
      throw new IllegalStateException();
    }
    SetDto set = (SetDto) ast;
    
    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> name;

    if (set.setName().getToken() == UcdlToken.VALUE_REFERENCE) {
      name = convertValueReference(set.setName());
    } else {
      name = convertElement(set.setName());
    }

    List<Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>> modifiers =
        new ArrayList<>();

    for (AbstractSyntaxTreeDto modifier : set.modifiers()) {
      if (modifier.getToken() == UcdlToken.ATTRIBUTE) {
        modifiers.add(convertAttribute(modifier));
      } else {
        modifiers.add(convertFilter(modifier));
      }
    }

    return (l) -> {
      Set<ResourceTimefoldInstance> reference = new HashSet<>();
      reference.add(name.apply(l));

      for (Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> function : modifiers) {
        reference = function.apply(reference);
      }
      if (reference.size() != 1) {
        throw new IllegalStateException();
      }
      return reference;
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertNumberSet(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.NUMBER_SET) {
      throw new IllegalStateException();
    }
    ValueDto<Integer[]> set = (ValueDto<Integer[]>) ast;

    Set<ResourceTimefoldInstance> resources = new HashSet<>();

    for (Integer i : set.value()) {
      resources.add(new Number(i));
    }

    return (l) -> resources;
  }

  //In this case the given List is the List is the List of elements of the set, and it returns the
  //set after applying the attribute.
  private static Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertAttribute(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.ATTRIBUTE) {
      throw new IllegalStateException();
    }

    String attribute = ((ValueDto<String>) ast).value();

    return switch (attribute) {
      case "index" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(new Number(((LessonTimefoldInstance) resource).getIndex()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "teacher" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getTeacher());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "timeslot" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getTimeslot());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "room" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getRoom());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "subject" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getSubject());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "studentGroup" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(((LessonTimefoldInstance) resource).getStudentGroup());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "grade" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.STUDENT_GROUP) {
            resultSet.add(((StudentGroupTimefoldInstance) resource).getGrade());
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "day" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.TIMESLOT) {
            resultSet.add(new Number(((TimeslotTimefoldInstance) resource).getDayOfWeek()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "slot" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
          if (resource.getResourceType() == ResourceType.TIMESLOT) {
            resultSet.add(new Number(((TimeslotTimefoldInstance) resource).getSlotId()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "students" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      case "teachers" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      case "studentGroups" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      case "rooms" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      case "subjects" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      case "grades" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      case "timeslots" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      case "lessons" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      case "tags" -> (l) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : l) {
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
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() == UcdlToken.NUMBER || ast.getToken() == UcdlToken.SIZE) {
      Function<List<ResourceTimefoldInstance>, Number> function = convertNumberElement(ast);
      return (l) -> (ResourceTimefoldInstance) function.apply(l);
    }

    if (ast.getToken() != UcdlToken.ELEMENT) {
      throw new IllegalStateException();
    }
    ElementDto element = (ElementDto) ast;

    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> name =
        convertValueReference(element.name());

    List<Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>> attributes =
        new ArrayList<>();

    for (AbstractSyntaxTreeDto attribute : element.attributes()) {
      attributes.add(convertAttribute(attribute));
    }

    return (l) -> {
      Set<ResourceTimefoldInstance> reference = new HashSet<>();
      reference.add(name.apply(l));

      for (Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> function : attributes) {
        reference = function.apply(reference);
      }
      if (reference.size() != 1) {
        throw new IllegalStateException();
      }
      return reference.iterator().next();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Number> convertNumberElement(
      AbstractSyntaxTreeDto ast) {
    return switch (ast.getToken()) {
      case NUMBER -> convertNumber(ast);
      case SIZE -> convertSize(ast);
      default -> throw new IllegalStateException();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, Number> convertNumber(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.NUMBER) {
      throw new IllegalStateException();
    }
    ValueDto<Integer> value = (ValueDto<Integer>) ast;
    return (l) -> new Number(value.value());
  }

  private static Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> convertValueReference(
      AbstractSyntaxTreeDto ast) {
    if (ast.getToken() != UcdlToken.VALUE_REFERENCE) {
      throw new IllegalStateException();
    }
    ValueDto<String> reference = (ValueDto<String>) ast;
    Iterator<String> iterator = params.sequencedKeySet().iterator();
    int index = 0;
    while (iterator.hasNext()) {
      if (iterator.next().equals(reference.value())) {
        break;
      }
      index++;
    }
    int finalIndex = index;
    return (l) -> {
      ResourceTimefoldInstance resource = l.get(finalIndex);
      if (resource.getResourceType() != params.get(reference.value())) {
        throw new IllegalArgumentException();
      }
      return l.get(finalIndex);
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
  RESOURCE_SET, //-> valuedto       -
  NUMBER_SET, //-> valuedto         x
  ATTRIBUTE, //-> valuedto          x
  ELEMENT, //-> valuedto            x
  NUMBER, //-> valuedto             x
  VALUE_REFERENCE //-> valuedto     x
   */
}
