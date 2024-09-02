package de.uftos.repositories.solver.timefold.constraints;

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
import de.uftos.repositories.solver.timefold.domain.GradeTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.LessonTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.NumberTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.RoomTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentGroupTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.SubjectTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TagTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TeacherTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimeslotTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimetableSolutionTimefoldInstance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * This factory creates constraint definitions for use with the Timefold-solver
 * based on ConstraintDefinitionDtos.
 */
public class ConstraintDefinitionFactory {

  /**
   * Creates a constraint definition based on a ConstraintDefinitionDto.
   *
   * @param definition the ConstraintDefinitionDto.
   * @return a ConstraintDefinitionTimefoldInstance representing the given ConstraintDefinitionDto.
   */
  public static ConstraintDefinitionTimefoldInstance getConstraintDefinition(
      ConstraintDefinitionDto definition) {
    String name = definition.name();
    RewardPenalize defaultType = definition.defaultType();

    LinkedHashMap<String, ResourceType> params = definition.parameters();

    AbstractSyntaxTreeDto ast = definition.root();

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(ast, params);

    return new ConstraintDefinitionTimefoldInstance(name, defaultType, function);
  }

  //The first parameter "List<ResourceTimefoldInstance>" of the returned function
  // always contains the arguments given to the constraint definition evaluation function.

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

    return (arguments) -> {
      for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> function : functions) {
        Optional<Boolean> bool = function.apply(arguments);
        if (bool.isPresent()) {
          return bool.get();
        }
      }
      return finalReturnValue.apply(arguments);
    };
  }

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  private static Function<
      List<ResourceTimefoldInstance>,
      Optional<Boolean>
      > convertFor(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.FOR) {
      throw new IllegalStateException();
    }

    ControlSequenceDto root = (ControlSequenceDto) ast;

    OperatorDto of = (OperatorDto) root.parenthesesContent();

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(of, params);

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    ResourceType setType;
    AbstractSyntaxTreeDto set = of.parameters().getLast();
    if (set.getToken() == UcdlToken.RESOURCE_SET) {
      setType = ((SetDto) set).type();
    } else if (set.getToken() == UcdlToken.ELEMENT) {
      setType = ((ElementDto) set).type();
    } else if (set.getToken() == UcdlToken.NUMBER || set.getToken() == UcdlToken.NUMBER_SET) {
      setType = ResourceType.NUMBER;
    } else {
      //should be impossible to reach as convertOf should already have thrown an exception
      throw new IllegalStateException();
    }

    params.put(variableReference.value(), setType);

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

    return (arguments) -> {
      Optional<Boolean> current = Optional.empty();
      for (ResourceTimefoldInstance resource : variableDefinition.apply(arguments)) {
        for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> function : functions) {
          arguments.add(resource);
          current = function.apply(arguments);
          arguments.remove(resource);
          if (current.isPresent()) {
            return current;
          }
        }
      }
      return current;
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Optional<Boolean>
      > convertIf(
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

    return (arguments) -> {
      Optional<Boolean> current = Optional.empty();
      if (condition.apply(arguments)) {
        for (Function<List<ResourceTimefoldInstance>, Optional<Boolean>> function : functions) {
          current = function.apply(arguments);
          if (current.isPresent()) {
            return current;
          }
        }
      }
      return current;
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertReturn(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.RETURN) {
      throw new IllegalStateException();
    }
    boolean value = ((ValueDto<Boolean>) ast).value();

    return (arguments) -> value;
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertBool(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return switch (ast.getToken()) {
      case CODEBLOCK -> convertCodeblock(ast, params);
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

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertAnd(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.AND) {
      throw new IllegalStateException();
    }

    OperatorDto and = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Boolean>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : and.parameters()) {
      functions.add(convertBool(child, params));
    }

    return (arguments) -> {
      boolean returnValue = true;
      for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
        returnValue = returnValue && function.apply(arguments);
      }
      return returnValue;
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertOr(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.OR) {
      throw new IllegalStateException();
    }

    OperatorDto or = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Boolean>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto child : or.parameters()) {
      functions.add(convertBool(child, params));
    }

    return (arguments) -> {
      boolean returnValue = false;
      for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
        returnValue = returnValue || function.apply(arguments);
      }
      return returnValue;
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertNot(
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

    return (arguments) -> !bool.apply(arguments);
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertImplies(
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

    return (arguments) -> !firstParameter.apply(arguments) || secondParameter.apply(arguments);
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertForAll(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.FOR_ALL) {
      throw new IllegalStateException();
    }

    QuantifierDto root = (QuantifierDto) ast;

    OperatorDto of = (OperatorDto) root.elements();

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(of, params);

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(root.body(), params);

    params.remove(variableReference.value());

    return (arguments) -> {
      boolean and = true;
      for (ResourceTimefoldInstance resource : variableDefinition.apply(arguments)) {
        arguments.add(resource);
        and = and && function.apply(arguments);
        arguments.remove(resource);
      }
      return and;
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertExists(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.EXISTS) {
      throw new IllegalStateException();
    }

    QuantifierDto root = (QuantifierDto) ast;
    OperatorDto of = (OperatorDto) root.elements();

    Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> variableDefinition =
        convertOf(of, params);


    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(root.body(), params);

    params.remove(variableReference.value());

    return (arguments) -> {
      boolean or = false;
      for (ResourceTimefoldInstance resource : variableDefinition.apply(arguments)) {
        arguments.add(resource);
        or = or || function.apply(arguments);
        arguments.remove(resource);
      }
      return or;
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertBoolValue(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.BOOL_VALUE) {
      throw new IllegalStateException();
    }
    boolean value = ((ValueDto<Boolean>) ast).value();

    return (arguments) -> value;
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertEquation(
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

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertEquals(
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

    return (arguments) -> {
      ResourceTimefoldInstance first = firstElement.apply(arguments);
      ResourceTimefoldInstance second = secondElement.apply(arguments);

      if (first.getResourceType() != second.getResourceType()) {
        throw new IllegalArgumentException();
      }

      return first.equals(second);
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertNotEquals(
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

    return (arguments) -> {
      ResourceTimefoldInstance first = firstElement.apply(arguments);
      ResourceTimefoldInstance second = secondElement.apply(arguments);

      if (first.getResourceType() != second.getResourceType()) {
        throw new IllegalArgumentException();
      }

      return !first.equals(second);
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertSmallerEquals(
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

    return (arguments) -> {
      ResourceTimefoldInstance first = firstElement.apply(arguments);
      ResourceTimefoldInstance second = secondElement.apply(arguments);

      if (first.getResourceType() != ResourceType.NUMBER
          || second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((NumberTimefoldInstance) first).getValue()
          <= ((NumberTimefoldInstance) second).getValue();
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertSmaller(
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

    return (arguments) -> {
      ResourceTimefoldInstance first = firstElement.apply(arguments);
      ResourceTimefoldInstance second = secondElement.apply(arguments);

      if (first.getResourceType() != ResourceType.NUMBER
          || second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((NumberTimefoldInstance) first).getValue()
          < ((NumberTimefoldInstance) second).getValue();
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertGreaterEquals(
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

    return (arguments) -> {
      ResourceTimefoldInstance first = firstElement.apply(arguments);
      ResourceTimefoldInstance second = secondElement.apply(arguments);

      if (first.getResourceType() != ResourceType.NUMBER
          || second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((NumberTimefoldInstance) first).getValue()
          >= ((NumberTimefoldInstance) second).getValue();
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertGreater(
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

    return (arguments) -> {
      ResourceTimefoldInstance first = firstElement.apply(arguments);
      ResourceTimefoldInstance second = secondElement.apply(arguments);

      if (first.getResourceType() != ResourceType.NUMBER
          || second.getResourceType() != ResourceType.NUMBER) {
        throw new IllegalArgumentException();
      }

      return ((NumberTimefoldInstance) first).getValue()
          > ((NumberTimefoldInstance) second).getValue();
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertIn(
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

    return (arguments) -> set.apply(arguments).contains(element.apply(arguments));
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Set<ResourceTimefoldInstance>
      > convertOf(
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

  private static Function<
      List<ResourceTimefoldInstance>,
      Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>
      > convertFilter(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.FILTER) {
      throw new IllegalStateException();
    }

    OperatorDto filter = (OperatorDto) ast;

    List<Function<List<ResourceTimefoldInstance>, Boolean>> functions = new ArrayList<>();

    for (AbstractSyntaxTreeDto param : filter.parameters()) {
      if (param.getToken() == UcdlToken.NUMBER_SET || param.getToken() == UcdlToken.RESOURCE_SET
          || param.getToken() == UcdlToken.ELEMENT || param.getToken() == UcdlToken.NUMBER) {
        Function<List<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> set =
            convertSet(param, params);
        Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> element =
            convertValueReference(new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this"), params);
        functions.add((parameters -> set.apply(parameters).contains(element.apply(parameters))));
      } else {
        functions.add(convertBool(param, params));
      }
    }

    return (arguments -> set -> {
      ResourceTimefoldInstance thisPointer = arguments.getFirst();
      Set<ResourceTimefoldInstance> filteredSet = new HashSet<>();
      for (ResourceTimefoldInstance resource : set) {
        arguments.set(0, resource); //replacing "this"-pointer

        boolean skipResource = false;
        for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
          if (!function.apply(arguments)) {
            skipResource = true;
            break;
          }
        }
        if (!skipResource) {
          filteredSet.add(resource);
        }
      }

      arguments.set(0, thisPointer);

      return filteredSet;
    });
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Boolean
      > convertIsEmpty(
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

    return (arguments) -> set.apply(arguments).isEmpty();
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      NumberTimefoldInstance
      > convertSize(
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

    return (arguments) -> new NumberTimefoldInstance(set.apply(arguments).size());
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Set<ResourceTimefoldInstance>
      > convertSet(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    switch (ast.getToken()) {
      case RESOURCE_SET -> {
        return convertResourceSet(ast, params);
      }
      case NUMBER_SET -> {
        return convertNumberSet(ast, params);
      }
      default -> {
        Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> element =
            convertElement(ast, params);
        return arguments -> {
          Set<ResourceTimefoldInstance> set = new HashSet<>();
          set.add(element.apply(arguments));
          return set;
        };
      }
    }
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Set<ResourceTimefoldInstance>
      > convertResourceSet(AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
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

    List<Function<
        List<ResourceTimefoldInstance>,
        Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>
        >> modifiers = new ArrayList<>();

    for (AbstractSyntaxTreeDto modifier : set.modifiers()) {
      if (modifier.getToken() == UcdlToken.ATTRIBUTE) {
        modifiers.add(convertAttribute(modifier, params));
      } else {
        modifiers.add(convertFilter(modifier, params));
      }
    }

    return (arguments) -> {
      Set<ResourceTimefoldInstance> setReference = new HashSet<>();
      setReference.add(name.apply(arguments));

      for (Function<
            List<ResourceTimefoldInstance>,
            Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>
            > function : modifiers) {
        setReference = function.apply(arguments).apply(setReference);
      }
      return setReference;
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Set<ResourceTimefoldInstance>
      > convertNumberSet(AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.NUMBER_SET) {
      throw new IllegalStateException();
    }
    ValueDto<Integer[]> set = (ValueDto<Integer[]>) ast;

    Set<ResourceTimefoldInstance> resources = new HashSet<>();

    for (int i : set.value()) {
      resources.add(new NumberTimefoldInstance(i));
    }

    return (arguments) -> resources;
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>
      > convertAttribute(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.ATTRIBUTE) {
      throw new IllegalStateException();
    }

    String attribute = ((ValueDto<String>) ast).value();

    return switch (attribute) {
      case "index" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.LESSON) {
            resultSet.add(
                new NumberTimefoldInstance(((LessonTimefoldInstance) resource).getIndex()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "teacher" -> (arguments) -> (set) -> {
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
      case "timeslot" -> (arguments) -> (set) -> {
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
      case "room" -> (arguments) -> (set) -> {
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
      case "subject" -> (arguments) -> (set) -> {
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
      case "studentGroup" -> (arguments) -> (set) -> {
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
      case "grade" -> (arguments) -> (set) -> {
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
      case "day" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMESLOT) {
            resultSet.add(
                new NumberTimefoldInstance(((TimeslotTimefoldInstance) resource).getDayOfWeek()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "slot" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          if (resource.getResourceType() == ResourceType.TIMESLOT) {
            resultSet.add(
                new NumberTimefoldInstance(((TimeslotTimefoldInstance) resource).getSlotId()));
          } else {
            throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "students" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getStudents());
            case TAG -> resultSet.addAll(((TagTimefoldInstance) resource).getStudentList());
            case STUDENT_GROUP ->
                resultSet.addAll(((StudentGroupTimefoldInstance) resource).getStudentList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "teachers" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getTeachers());
            case TAG -> resultSet.addAll(((TagTimefoldInstance) resource).getTeacherList());
            case SUBJECT -> resultSet.addAll(((SubjectTimefoldInstance) resource).getTeacherList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "studentGroups" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getStudentGroups());
            case TAG -> resultSet.addAll(((TagTimefoldInstance) resource).getStudentGroupList());
            case GRADE ->
                resultSet.addAll(((GradeTimefoldInstance) resource).getStudentGroupList());
            case STUDENT ->
                resultSet.addAll(((StudentTimefoldInstance) resource).getStudentGroupList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "rooms" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getRooms());
            case TAG -> resultSet.addAll(((TagTimefoldInstance) resource).getRoomList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "subjects" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getSubjects());
            case TAG -> resultSet.addAll(((TagTimefoldInstance) resource).getSubjectList());
            case TEACHER -> resultSet.addAll(((TeacherTimefoldInstance) resource).getSubjectList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "grades" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getGrades());
            case TAG -> resultSet.addAll(((TagTimefoldInstance) resource).getGradeList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "timeslots" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getTimeslots());
            case TAG -> resultSet.addAll(((TagTimefoldInstance) resource).getTimeslotList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "lessons" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getLessons());
            case TEACHER -> resultSet.addAll(((TeacherTimefoldInstance) resource).getLessonList());
            case STUDENT_GROUP ->
                resultSet.addAll(((StudentGroupTimefoldInstance) resource).getLessonList());
            case ROOM -> resultSet.addAll(((RoomTimefoldInstance) resource).getLessonList());
            case SUBJECT -> resultSet.addAll(((SubjectTimefoldInstance) resource).getLessonList());
            case TIMESLOT ->
                resultSet.addAll(((TimeslotTimefoldInstance) resource).getLessonList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      case "tags" -> (arguments) -> (set) -> {
        Set<ResourceTimefoldInstance> resultSet = new HashSet<>();
        for (ResourceTimefoldInstance resource : set) {
          switch (resource.getResourceType()) {
            case TIMETABLE ->
                resultSet.addAll(((TimetableSolutionTimefoldInstance) resource).getTags());
            case STUDENT ->
                resultSet.addAll(((StudentTimefoldInstance) resource).getProvidedTagsList());
            case TEACHER ->
                resultSet.addAll(((TeacherTimefoldInstance) resource).getProvidedTagsList());
            case STUDENT_GROUP ->
                resultSet.addAll(((StudentGroupTimefoldInstance) resource).getProvidedTagsList());
            case ROOM -> resultSet.addAll(((RoomTimefoldInstance) resource).getProvidedTagsList());
            case SUBJECT ->
                resultSet.addAll(((SubjectTimefoldInstance) resource).getProvidedTagsList());
            case GRADE ->
                resultSet.addAll(((GradeTimefoldInstance) resource).getProvidedTagsList());
            case TIMESLOT ->
                resultSet.addAll(((TimeslotTimefoldInstance) resource).getProvidedTagsList());
            default -> throw new IllegalStateException();
          }
        }
        return resultSet;
      };
      default -> throw new IllegalStateException();
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      ResourceTimefoldInstance
      > convertElement(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() == UcdlToken.NUMBER || ast.getToken() == UcdlToken.SIZE) {
      Function<List<ResourceTimefoldInstance>, NumberTimefoldInstance> function =
          convertNumberElement(ast, params);

      return (arguments) -> (ResourceTimefoldInstance) function.apply(arguments);
    }

    if (ast.getToken() != UcdlToken.ELEMENT) {
      throw new IllegalStateException();
    }
    ElementDto element = (ElementDto) ast;

    Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> name =
        convertValueReference(element.name(), params);

    List<Function<
        List<ResourceTimefoldInstance>,
        Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>
        >> attributes = new ArrayList<>();

    for (AbstractSyntaxTreeDto attribute : element.attributes()) {
      attributes.add(convertAttribute(attribute, params));
    }

    return (arguments) -> {
      Set<ResourceTimefoldInstance> elementReference = new HashSet<>();
      elementReference.add(name.apply(arguments));

      for (Function<
            List<ResourceTimefoldInstance>,
            Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>>
            > function : attributes) {
        elementReference = function.apply(arguments).apply(elementReference);
      }
      if (elementReference.size() != 1) {
        throw new IllegalStateException();
      }
      return elementReference.iterator().next();
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      NumberTimefoldInstance
      > convertNumberElement(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return switch (ast.getToken()) {
      case NUMBER -> convertNumber(ast, params);
      case SIZE -> convertSize(ast, params);
      default -> throw new IllegalStateException();
    };
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      NumberTimefoldInstance
      > convertNumber(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.NUMBER) {
      throw new IllegalStateException();
    }
    ValueDto<Integer> value = (ValueDto<Integer>) ast;

    return (arguments) -> new NumberTimefoldInstance(value.value());
  }

  private static Function<
      List<ResourceTimefoldInstance>,
      ResourceTimefoldInstance
      > convertValueReference(AbstractSyntaxTreeDto ast,
                              LinkedHashMap<String, ResourceType> params) {
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

    return (arguments) -> arguments.get(finalIndex);
  }
}
