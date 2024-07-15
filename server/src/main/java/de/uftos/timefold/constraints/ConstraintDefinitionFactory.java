package de.uftos.timefold.constraints;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.UcdlToken;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.dto.ucdl.ast.ControlSequenceDto;
import de.uftos.dto.ucdl.ast.OperatorDto;
import de.uftos.dto.ucdl.ast.QuantifierDto;
import de.uftos.dto.ucdl.ast.SetDto;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.timefold.domain.Number;
import de.uftos.timefold.domain.ResourceTimefoldInstance;
import java.util.ArrayList;
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

    AbstractSyntaxTreeDto ast = definition.root();

    if (ast.getToken() == UcdlToken.CODEBLOCK) {
      function = convertCodeblock(ast, definition.parameters());
    } else {
      function = convertBool(ast, definition.parameters());
    }

    return new ConstraintDefinitionTimefoldInstance(name, defaultType, function);
  }

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
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.FOR) {
      throw new IllegalStateException();
    }

    ControlSequenceDto root = (ControlSequenceDto) ast;

    Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> variableDefinition =
        convertOf(root.parenthesesContent(), params);

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
        functions.add(convertFor(child, params));
      } else if (child.getToken() == UcdlToken.IF) {
        functions.add(convertIf(child, params));
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
        functions.add(convertFor(child, params));
      } else if (child.getToken() == UcdlToken.IF) {
        functions.add(convertIf(child, params));
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
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.RETURN) {
      throw new IllegalStateException();
    }
    boolean value = ((ValueDto<Boolean>) ast).value();
    return (l) -> value;
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

    return (l) -> {
      boolean returnValue = true;
      for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
        returnValue = returnValue && function.apply(l);
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

    return (l) -> {
      boolean returnValue = false;
      for (Function<List<ResourceTimefoldInstance>, Boolean> function : functions) {
        returnValue = returnValue || function.apply(l);
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

    return (l) -> !bool.apply(l);
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

    return (l) -> !firstParameter.apply(l) || secondParameter.apply(l);
  }

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertForAll(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.FOR_ALL) {
      throw new IllegalStateException();
    }

    QuantifierDto root = (QuantifierDto) ast;

    Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> variableDefinition =
        convertOf(root.elements(), params);

    OperatorDto of = (OperatorDto) root.elements();

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(root.body(), params);

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
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.EXISTS) {
      throw new IllegalStateException();
    }

    QuantifierDto root = (QuantifierDto) ast;

    Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> variableDefinition =
        convertOf(root.elements(), params);

    OperatorDto of = (OperatorDto) root.elements();

    if (of.parameters().size() != 2) {
      throw new IllegalStateException();
    }

    ValueDto<String> variableReference = (ValueDto<String>) of.parameters().getFirst();
    SetDto set = (SetDto) of.parameters().getLast();

    params.put(variableReference.value(), set.type());

    Function<List<ResourceTimefoldInstance>, Boolean> function = convertBool(root.body(), params);

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
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.BOOL_VALUE) {
      throw new IllegalStateException();
    }
    boolean value = ((ValueDto<Boolean>) ast).value();
    return (l) -> value;
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
    return (l) -> firstElement.apply(l).equals(secondElement.apply(l));
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
    return (l) -> !firstElement.apply(l).equals(secondElement.apply(l));
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
    Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> set =
        convertSet(in.parameters().getLast(), params);
    return (l) -> set.apply(l).contains(element.apply(l));
  }

  private static Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> convertOf(
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

  private static Function<List<ResourceTimefoldInstance>, Boolean> convertFilter(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
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
    Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> set =
        convertSet(isEmpty.parameters().getFirst(), params);
    return (l) -> set.apply(l).isEmpty();
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
    Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> set =
        convertSet(size.parameters().getFirst(), params);
    return (l) -> new Number(set.apply(l).size());
  }

  private static Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> convertSet(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return switch (ast.getToken()) {
      case RESOURCE_SET -> convertResourceSet(ast, params);
      case NUMBER_SET -> convertNumberSet(ast, params);
      default -> throw new IllegalStateException();
    };
  }

  private static Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> convertResourceSet(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return null;
  }

  private static Function<List<ResourceTimefoldInstance>, List<ResourceTimefoldInstance>> convertNumberSet(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return null;
  }

  private static Function<Set<ResourceTimefoldInstance>, Set<ResourceTimefoldInstance>> convertAttribute(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return null;
  }

  private static Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> convertElement(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    return null;
  }

  private static Function<List<ResourceTimefoldInstance>, Number> convertNumber(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
    if (ast.getToken() != UcdlToken.NUMBER) {
      throw new IllegalStateException();
    }
    ValueDto<Integer> value = (ValueDto<Integer>) ast;
    return (l) -> new Number(value.value());
  }

  private static Function<List<ResourceTimefoldInstance>, ResourceTimefoldInstance> convertValueReference(
      AbstractSyntaxTreeDto ast, LinkedHashMap<String, ResourceType> params) {
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
  NUMBER_SET, //-> valuedto         -
  ATTRIBUTE, //-> valuedto          -
  ELEMENT, //-> valuedto            -
  NUMBER, //-> valuedto             x
  VALUE_REFERENCE //-> valuedto     x
   */
}
