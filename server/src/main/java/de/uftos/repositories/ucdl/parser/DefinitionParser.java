package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.UcdlToken;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.dto.ucdl.ast.ControlSequenceDto;
import de.uftos.dto.ucdl.ast.ElementDto;
import de.uftos.dto.ucdl.ast.OperatorDto;
import de.uftos.dto.ucdl.ast.QuantifierDto;
import de.uftos.dto.ucdl.ast.SetDto;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.repositories.ucdl.parser.javacc.Node;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import de.uftos.repositories.ucdl.parser.javacc.SimpleNode;
import de.uftos.repositories.ucdl.parser.javacc.SyntaxChecker;
import de.uftos.repositories.ucdl.parser.javacc.TokenMgrError;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//magic numbers are tied directly to the used grammar
//(see ucdl.jjt in server/src/main/resources/ucdl.jjt)

/**
 * This class parses the ucdl-code of the "definition"-field of a constraint definition and returns
 * the abstract syntax tree, which resembles the code.
 */
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
public class DefinitionParser {
  /**
   * Parses the given definition using the given parameters.
   *
   * @param definition the ucdl-code of the "definition"-field.
   * @param parameters a HashMap containing the names and types of the given parameters.
   * @return the abstract syntax tree which resembles the given ucdl-code:
   * @throws ParseException if the given ucdl-code isn't correct.
   */
  public static AbstractSyntaxTreeDto parseDefinition(String definition,
                                                      LinkedHashMap<String, ResourceType> parameters)
      throws ParseException {
    LinkedHashMap<String, ResourceType> params = new LinkedHashMap<>(parameters);

    try {
      SimpleNode root = SyntaxChecker.parseString(definition);
      return buildAst(root, params);
    } catch (TokenMgrError e) {
      throw new ParseException(e.getMessage());
    }
  }

  private static AbstractSyntaxTreeDto buildAst(Node root,
                                                LinkedHashMap<String, ResourceType> parameters)
      throws ParseException {
    String s = root.toString();
    switch (s) {
      case "START", "BOOL", "DEFINITION", "CONTROLSEQUENCE" -> {
        return buildAst(root.jjtGetChild(0), parameters);
      }
      case "BOOLVALUE" -> {
        return new ValueDto<>(UcdlToken.BOOL_VALUE,
            Boolean.parseBoolean(((SimpleNode) root).jjtGetFirstToken().image));
      }
      case "CODEBLOCK" -> { //list of control sequences followed by one return
        List<AbstractSyntaxTreeDto> params = new ArrayList<>();

        Node controlSequenceList = root.jjtGetChild(0); //list of control sequences
        while (controlSequenceList.jjtGetNumChildren() == 2) {
          params.add(buildAst(controlSequenceList.jjtGetChild(0), parameters));
          controlSequenceList = controlSequenceList.jjtGetChild(1);
        }
        params.add(buildAst(root.jjtGetChild(1), parameters)); //return

        //semantic check for legal return values
        // returned value is irrelevant, only error checking
        getReturnValue(params);
        return new OperatorDto(UcdlToken.CODEBLOCK, params);
      }
      case "RETURN" -> {
        SimpleNode boolVal = (SimpleNode) root.jjtGetChild(0);
        return new ValueDto<>(UcdlToken.RETURN,
            Boolean.parseBoolean(boolVal.jjtGetFirstToken().image));
      }
      case "FOR" -> {
        List<AbstractSyntaxTreeDto> variableDefinition = new ArrayList<>();
        ValueDto<String> varName = (ValueDto<String>) buildAst(root.jjtGetChild(0), parameters);
        variableDefinition.add(varName); //variable name
        if (parameters.containsKey(varName.value())) {
          throw new ParseException("Variable name \"" + varName.value()
              + "\" does already exist inside this namespace!");
        }
        SetDto set = (SetDto) buildAst(root.jjtGetChild(1), parameters);
        variableDefinition.add(set); //set
        OperatorDto variable = new OperatorDto(UcdlToken.OF, variableDefinition);

        parameters.put(varName.value(), set.type()); //adding the new variable

        //body
        List<AbstractSyntaxTreeDto> body = new ArrayList<>();
        body.add(buildAst(root.jjtGetChild(2), parameters)); //first control sequence

        Node controlSequenceList = root.jjtGetChild(3); //rest of the control sequences
        while (controlSequenceList.jjtGetNumChildren() == 2) {
          body.add(buildAst(controlSequenceList.jjtGetChild(0), parameters));
          controlSequenceList = controlSequenceList.jjtGetChild(1);
        }

        parameters.remove(varName.value()); //removing the new variable

        //semantic check for legal return values
        boolean returnValue = getReturnValue(body);
        return new ControlSequenceDto(UcdlToken.FOR, variable, body, returnValue);
      }
      case "IF" -> {
        AbstractSyntaxTreeDto bool = buildAst(root.jjtGetChild(0), parameters);

        List<AbstractSyntaxTreeDto> body = new ArrayList<>();

        Node bodyElements = root.jjtGetChild(1);

        switch (bodyElements.jjtGetChild(0).toString()) {
          case "RETURN" -> {
            ValueDto<Boolean> dto =
                (ValueDto<Boolean>) buildAst(bodyElements.jjtGetChild(0), parameters);
            return new ControlSequenceDto(UcdlToken.IF, bool, body, dto.value());
          }
          case "CONTROLSEQUENCE" -> {
            body.add(buildAst(bodyElements.jjtGetChild(0), parameters)); //first control sequence

            Node controlSequenceList = bodyElements.jjtGetChild(1); //rest of the control sequences
            while (controlSequenceList.jjtGetNumChildren() == 2) {
              body.add(buildAst(controlSequenceList.jjtGetChild(0), parameters));
              controlSequenceList = controlSequenceList.jjtGetChild(1);
            }

            //getReturnValue() includes a semantic check for legal return values
            return new ControlSequenceDto(UcdlToken.IF, bool, body, getReturnValue(body));
          }
          default -> throw new IllegalStateException();
        }
      }
      case "FOR_ALL" -> {
        return buildQuantifier(UcdlToken.FOR_ALL, root, parameters);
      }
      case "EXISTS" -> {
        return buildQuantifier(UcdlToken.EXISTS, root, parameters);
      }
      case "IMPLIES" -> {
        return buildBinaryOperator(UcdlToken.IMPLIES, root, parameters);
      }
      case "OR" -> {
        return buildBinaryOperator(UcdlToken.OR, root, parameters);
      }
      case "AND" -> {
        return buildBinaryOperator(UcdlToken.AND, root, parameters);
      }
      case "NOT" -> {
        if (((SimpleNode) root).jjtGetFirstToken().image.equals("NOT")) {
          return buildUnaryOperator(UcdlToken.NOT, root, parameters);
        }
        return buildAst(root.jjtGetChild(0), parameters);
      }
      case "IS_EMPTY" -> {
        return buildUnaryOperator(UcdlToken.IS_EMPTY, root, parameters);
      }
      case "ELEMENT_IN_SET_OR_EQUATION" -> {
        List<AbstractSyntaxTreeDto> params = new ArrayList<>();
        AbstractSyntaxTreeDto element = buildAst(root.jjtGetChild(0), parameters);
        ResourceType elementType = getElementResourceType(element);
        params.add(element);

        Node secondPart = root.jjtGetChild(1);

        if (secondPart.toString().equals("ELEMENT_IN_SET")) {
          AbstractSyntaxTreeDto set = buildAst(secondPart.jjtGetChild(0), parameters);
          ResourceType setType = getSetResourceType(set);
          params.add(set);

          if (elementType != setType) {
            throw new ParseException("Elements can only be in Sets of the same type!");
          }
          return new OperatorDto(UcdlToken.IN, params);
        }

        if (secondPart.toString().equals("EQUATION")) {
          params.add(buildAst(secondPart.jjtGetChild(1), parameters));
          UcdlToken token = getComparatorToken(
              ((SimpleNode) secondPart.jjtGetChild(0)).jjtGetFirstToken().image,
              params
          );
          return new OperatorDto(token, params);
        }
        throw new IllegalStateException();
      }
      case "ELEMENT" -> {
        if (root.jjtGetChild(0).toString().equals("NUMBERELEMENT")) {
          return buildAst(root.jjtGetChild(0), parameters);
        }

        ValueDto<String> elementName = (ValueDto<String>) buildAst(root.jjtGetChild(0), parameters);

        ResourceType elementType = parameters.get(elementName.value());

        List<AbstractSyntaxTreeDto> attributes = new ArrayList<>();

        Node attributeList = root.jjtGetChild(1);
        while (attributeList.jjtGetNumChildren() > 0) {
          String attribute = ((SimpleNode) attributeList).jjtGetFirstToken().next.image;
          elementType = getNextResourceType(elementType, attribute);
          //^ also performs semantic check whether the attribute is applicable to the given type
          attributes.add(new ValueDto<>(UcdlToken.ATTRIBUTE, attribute));
          attributeList = attributeList.jjtGetChild(0);
        }
        return new ElementDto(UcdlToken.ELEMENT, elementName, attributes, elementType);
      }
      case "ELEMENT_NAME" -> {
        String image = ((SimpleNode) root).jjtGetFirstToken().image;
        if (!parameters.containsKey(image)) {
          throw new ParseException("Parameter/Variable \"" + image + "\" does not exist!");
        }
        return new ValueDto<>(UcdlToken.VALUE_REFERENCE, image);
      }
      case "NUMBERELEMENT" -> {
        if (((SimpleNode) root).jjtGetFirstToken().image.equals("size")) {
          return buildUnaryOperator(UcdlToken.SIZE, root, parameters);
        } else {
          return new ValueDto<>(UcdlToken.NUMBER, Integer.parseInt(
              ((SimpleNode) root).jjtGetFirstToken().image));
        }
      }
      case "SET" -> {
        AbstractSyntaxTreeDto setName = buildAst(root.jjtGetChild(0).jjtGetChild(0), parameters);

        ResourceType setType = getSetResourceType(setName);

        return buildSet(setName, setType, root.jjtGetChild(1), parameters);

      }
      case "NUMBER_SET" -> {
        List<Integer> values = new ArrayList<>();

        values.add(Integer.parseInt(((SimpleNode) root).jjtGetFirstToken().next.image));

        SimpleNode numberList = (SimpleNode) root.jjtGetChild(0);
        while (numberList.jjtGetNumChildren() > 0) {
          values.add(Integer.parseInt(numberList.jjtGetFirstToken().next.image));
          numberList = (SimpleNode) numberList.jjtGetChild(0);
        }
        return new ValueDto<>(UcdlToken.NUMBER_SET, values.toArray(new Integer[0]));
      }
      case "VALUE_REFERENCE" -> {
        //only used in variable definition (elementName is already taken care of

        String image = ((SimpleNode) root).jjtGetFirstToken().image;
        if (image.equals("this")) {
          throw new ParseException("\"this\" is reserved and can't be used as variable!");
        }
        return new ValueDto<>(UcdlToken.VALUE_REFERENCE, image);
      }
      case "FILTER" -> {
        switch (root.jjtGetChild(0).toString()) {
          case "BOOLVALUE", "FOR_ALL", "EXISTS", "IS_EMPTY" -> {
            return buildAst(root.jjtGetChild(0), parameters);
          }
          case "NUMBER_SET" -> {
            AbstractSyntaxTreeDto numberSet = buildAst(root.jjtGetChild(0), parameters);

            ResourceType thisType = parameters.get("this");
            parameters.replace("this", ResourceType.NUMBER);

            List<AbstractSyntaxTreeDto> modifiers = new ArrayList<>();

            Node modifierList = root.jjtGetChild(1);
            while (modifierList.jjtGetNumChildren() > 0) {
              if (modifierList.jjtGetNumChildren() == 2) { //-> Flatmap
                throw new ParseException("Numbers don't have any attributes!");
              } else if (modifierList.jjtGetNumChildren() == 3) {
                List<AbstractSyntaxTreeDto> filters = new ArrayList<>();
                filters.add(buildAst(modifierList.jjtGetChild(0), parameters));

                Node filterList = modifierList.jjtGetChild(1);
                while (filterList.jjtGetNumChildren() > 0) {
                  filters.add(buildAst(filterList.jjtGetChild(0), parameters));
                  filterList = filterList.jjtGetChild(1);
                }
                for (AbstractSyntaxTreeDto filter : filters) {
                  if (filter.getToken() == UcdlToken.RESOURCE_SET
                      && ((SetDto) filter).type() != ResourceType.NUMBER) {
                    throw new ParseException(
                        "Sets can only be filtered using sets of the same type!");
                  }
                }
                modifiers.add(new OperatorDto(UcdlToken.FILTER, filters));
              }
            }
            parameters.replace("this", thisType);
            return new SetDto(UcdlToken.RESOURCE_SET, ResourceType.NUMBER, numberSet, modifiers);
          }
          case "ELEMENT" -> {
            switch (root.jjtGetChild(1).toString()) {
              case "SET_MODIFICATION" -> {
                AbstractSyntaxTreeDto setName = buildAst(root.jjtGetChild(0), parameters);
                ResourceType setType = getSetResourceType(setName);
                return buildSet(setName, setType, root.jjtGetChild(1), parameters);
              }
              case "ELEMENT_IN_SET", "EQUATION" -> {
                String operator = root.jjtGetChild(1).toString();
                AbstractSyntaxTreeDto ast;

                List<AbstractSyntaxTreeDto> params = new ArrayList<>();
                AbstractSyntaxTreeDto element = buildAst(root.jjtGetChild(0), parameters);
                ResourceType elementType = getElementResourceType(element);
                params.add(element);

                if (operator.equals("ELEMENT_IN_SET")) {
                  AbstractSyntaxTreeDto set =
                      buildAst(root.jjtGetChild(1).jjtGetChild(0), parameters);
                  ResourceType setType = getSetResourceType(set);
                  params.add(set);

                  if (elementType != setType) {
                    throw new ParseException("Elements can only be in Sets of the same type!");
                  }

                  ast = new OperatorDto(UcdlToken.IN, params);

                } else if (operator.equals("EQUATION")) {
                  Node secondElement = root.jjtGetChild(1).jjtGetChild(1);
                  String comparator =
                      ((SimpleNode) root.jjtGetChild(1).jjtGetChild(0)).jjtGetFirstToken().image;

                  params.add(buildAst(secondElement, parameters));
                  UcdlToken token = getComparatorToken(comparator, params);
                  ast = new OperatorDto(token, params);

                } else {
                  throw new IllegalStateException();
                }

                if (root.jjtGetNumChildren() == 2) {
                  return ast; //operator isn't followed by further optional boolean operations
                }

                //further optional boolean operations are following
                params = new ArrayList<>();
                params.add(ast);
                params.add(buildAst(root.jjtGetChild(2).jjtGetChild(0), parameters));
                return switch (((SimpleNode) root.jjtGetChild(2)).jjtGetFirstToken().image) {
                  case "implies", "->" -> new OperatorDto(UcdlToken.IMPLIES, params);
                  case "or", "||" -> new OperatorDto(UcdlToken.OR, params);
                  case "and", "&&" -> new OperatorDto(UcdlToken.AND, params);
                  default -> throw new IllegalStateException();
                };
              }
              default -> throw new IllegalStateException();
            }
          }
          default -> throw new IllegalStateException();

        }
      }
      //includes cases: "CONTROLSEQUENCE_LIST", "CONTROLSEQUENCE_RETURN",
      // "OPTIONAL_IMPLIES", "OPTIONAL_OR", "OPTIONAL_AND",
      // "ELEMENT_IN_SET", "EQUATION", "ELEMENT_EQUATION", "ELEMENT_ATTRIBUTE_LIST",
      // "SET_NAME", "SET_MODIFICATION", "NUMBER_LIST", "ATTRIBUTE", "FILTER_LIST"
      default -> throw new IllegalStateException();
    }
  }

  private static ResourceType getSetResourceType(AbstractSyntaxTreeDto set) {
    if (set.getToken() == UcdlToken.NUMBER || set.getToken() == UcdlToken.NUMBER_SET
        || set.getToken() == UcdlToken.SIZE) {
      return ResourceType.NUMBER;
    } else if (set.getToken() == UcdlToken.ELEMENT) {
      return ((ElementDto) set).type();
    } else {
      return ((SetDto) set).type();
    }
  }

  private static ResourceType getElementResourceType(AbstractSyntaxTreeDto element) {
    if (element.getToken() == UcdlToken.NUMBER
        || element.getToken() == UcdlToken.SIZE) {
      return ResourceType.NUMBER;
    } else {
      return ((ElementDto) element).type();
    }
  }

  private static AbstractSyntaxTreeDto buildSet(AbstractSyntaxTreeDto setName,
                                                ResourceType setType, Node modifierList,
                                                LinkedHashMap<String, ResourceType> parameters)
      throws ParseException {
    List<AbstractSyntaxTreeDto> modifiers = new ArrayList<>();

    while (modifierList.jjtGetNumChildren() > 0) {
      if (modifierList.jjtGetNumChildren() == 2) {
        SimpleNode flatmap = (SimpleNode) modifierList.jjtGetChild(0);
        String attribute = flatmap.jjtGetFirstToken().image;
        setType = getNextResourceType(setType, attribute);
        modifiers.add(new ValueDto<>(UcdlToken.ATTRIBUTE, attribute));
        modifierList = modifierList.jjtGetChild(1);

      } else if (modifierList.jjtGetNumChildren() == 3) {
        ResourceType thisType = parameters.get("this");
        parameters.replace("this", setType);

        List<AbstractSyntaxTreeDto> filters = new ArrayList<>();
        filters.add(buildAst(modifierList.jjtGetChild(0), parameters));

        Node filterList = modifierList.jjtGetChild(1);
        while (filterList.jjtGetNumChildren() > 0) {
          filters.add(buildAst(filterList.jjtGetChild(0), parameters));
          filterList = filterList.jjtGetChild(1);
        }

        for (AbstractSyntaxTreeDto filter : filters) {
          if (filter.getToken() == UcdlToken.NUMBER_SET && setType == ResourceType.NUMBER) {
            continue; //valid number set
          }
          if (filter.getToken() == UcdlToken.RESOURCE_SET
              && ((SetDto) filter).type() == setType) {
            continue; //valid resource set
          }
          if (filter.getToken() != UcdlToken.NUMBER_SET
              && filter.getToken() != UcdlToken.RESOURCE_SET) {
            continue; //bool
          }
          throw new ParseException("Sets can only be filtered using sets of the same type!");
        }

        modifiers.add(new OperatorDto(UcdlToken.FILTER, filters));
        modifierList = modifierList.jjtGetChild(2);
        parameters.replace("this", thisType);
      }
    }

    return new SetDto(UcdlToken.RESOURCE_SET, setType, setName, modifiers);
  }

  private static AbstractSyntaxTreeDto buildUnaryOperator(UcdlToken token, Node root,
                                                          LinkedHashMap<String, ResourceType> parameters)
      throws ParseException {
    List<AbstractSyntaxTreeDto> param = new ArrayList<>();
    param.add(buildAst(root.jjtGetChild(0), parameters));
    return new OperatorDto(token, param);
  }

  private static AbstractSyntaxTreeDto buildBinaryOperator(UcdlToken token, Node root,
                                                           LinkedHashMap<String, ResourceType> parameters)
      throws ParseException {
    AbstractSyntaxTreeDto firstArgument = buildAst(root.jjtGetChild(0), parameters);
    if (root.jjtGetChild(1).jjtGetNumChildren() > 0) {
      AbstractSyntaxTreeDto secondArgument =
          buildAst(root.jjtGetChild(1).jjtGetChild(0), parameters);
      List<AbstractSyntaxTreeDto> params = new ArrayList<>();
      params.add(firstArgument);
      params.add(secondArgument);
      return new OperatorDto(token, params);
    }
    return firstArgument;
  }

  private static QuantifierDto buildQuantifier(UcdlToken token, Node root,
                                               LinkedHashMap<String, ResourceType> parameters)
      throws ParseException {
    List<AbstractSyntaxTreeDto> variableDefinition = new ArrayList<>();
    ValueDto<String> varName = (ValueDto<String>) buildAst(root.jjtGetChild(0), parameters);
    variableDefinition.add(varName); //variable name
    if (parameters.containsKey(varName.value())) {
      throw new ParseException("Variable name \"" + varName.value()
          + "\" does already exist inside this namespace!");
    }
    SetDto set = (SetDto) buildAst(root.jjtGetChild(1), parameters);
    variableDefinition.add(set); //set
    OperatorDto variable = new OperatorDto(UcdlToken.OF, variableDefinition);

    parameters.put(varName.value(), set.type()); //adding the new variable
    AbstractSyntaxTreeDto body = buildAst(root.jjtGetChild(2), parameters);
    parameters.remove(varName.value()); //removing the new variable

    return new QuantifierDto(token, variable, body);
  }

  /**
   * Checks that all returnValues of the ControlSequences inside the body are the same
   * and returns that value.
   *
   * @param body the List of all ASTs inside the body.
   * @return the value these ASTs return.
   * @throws ParseException if the List contains ASTs which would return different values.
   */
  private static boolean getReturnValue(List<AbstractSyntaxTreeDto> body) throws ParseException {
    boolean and = true;
    boolean or = false;
    boolean noControlSequence = true;
    for (AbstractSyntaxTreeDto ast : body) {
      if (ast.getToken() == UcdlToken.FOR || ast.getToken() == UcdlToken.IF) {
        noControlSequence = false;
        ControlSequenceDto cs = (ControlSequenceDto) ast;
        and &= cs.returnValue();
        or |= cs.returnValue();
      }
    }
    if (!noControlSequence && (and != or)) {
      throw new ParseException(
          "All control sequences in the body of a control sequence need to return"
              + " the same boolean value!");
    }
    return noControlSequence || and;
  }

  private static UcdlToken getComparatorToken(String comparator, List<AbstractSyntaxTreeDto> params)
      throws ParseException {
    if (params.size() != 2) {
      throw new IllegalStateException();
    }

    UcdlToken token = switch (comparator) {
      case ">" -> UcdlToken.GREATER;
      case "<" -> UcdlToken.SMALLER;
      case ">=" -> UcdlToken.GREATER_EQUALS;
      case "<=" -> UcdlToken.SMALLER_EQUALS;
      case "==" -> UcdlToken.EQUALS;
      case "!=" -> UcdlToken.NOT_EQUALS;
      default -> throw new IllegalStateException();
    };

    List<ResourceType> types = new ArrayList<>(2);

    for (AbstractSyntaxTreeDto ast : params) {
      if (ast.getToken() == UcdlToken.NUMBER || ast.getToken() == UcdlToken.SIZE) {
        types.add(ResourceType.NUMBER);
      } else if (ast.getToken() == UcdlToken.ELEMENT) {
        types.add(((ElementDto) ast).type());
      } else {
        throw new IllegalStateException();
      }
    }

    if (types.get(0) != types.get(1)) {
      throw new ParseException("Only elements of the same type can be compared!");
    }

    if (token == UcdlToken.GREATER || token == UcdlToken.SMALLER
        || token == UcdlToken.GREATER_EQUALS || token == UcdlToken.SMALLER_EQUALS) {
      for (ResourceType type : types) {
        if (type != ResourceType.NUMBER) {
          throw new ParseException(
              "Illegal comparison argument! Only numbers can be compared using "
                  + "\"<\",\">\",\"<=\",\">=\"!");
        }
      }
    }
    return token;
  }

  private static ResourceType getNextResourceType(ResourceType currentType, String attribute)
      throws ParseException {
    return switch (attribute) {
      case "index" -> {
        if (currentType != ResourceType.LESSON) {
          throw new ParseException(
              "Attribute \"" + attribute + "\" is only available for lessons!");
        }
        yield ResourceType.NUMBER;
      }
      case "teacher" -> {
        if (currentType != ResourceType.LESSON) {
          throw new ParseException(
              "Attribute \"" + attribute + "\" is only available for lessons!");
        }
        yield ResourceType.TEACHER;
      }
      case "timeslot" -> {
        if (currentType != ResourceType.LESSON) {
          throw new ParseException(
              "Attribute \"" + attribute + "\" is only available for lessons!");
        }
        yield ResourceType.TIMESLOT;
      }
      case "room" -> {
        if (currentType != ResourceType.LESSON) {
          throw new ParseException(
              "Attribute \"" + attribute + "\" is only available for lessons!");
        }
        yield ResourceType.ROOM;
      }
      case "subject" -> {
        if (currentType != ResourceType.LESSON) {
          throw new ParseException(
              "Attribute \"" + attribute + "\" is only available for lessons!");
        }
        yield ResourceType.SUBJECT;
      }
      case "studentGroup" -> {
        if (currentType != ResourceType.LESSON) {
          throw new ParseException(
              "Attribute \"" + attribute + "\" is only available for lessons!");
        }
        yield ResourceType.STUDENT_GROUP;
      }
      case "grade" -> {
        if (currentType != ResourceType.STUDENT_GROUP) {
          throw new ParseException(
              "Attribute \"" + attribute + "\" is only available for studentGroups!");
        }
        yield ResourceType.GRADE;
      }
      case "day", "slot" -> {
        if (currentType != ResourceType.TIMESLOT) {
          throw new ParseException(
              "Attribute \"" + attribute + "\" is only available for timeslots!");
        }
        yield ResourceType.NUMBER;
      }
      case "students" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.STUDENT_GROUP
            && currentType != ResourceType.TAG) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters), studentGroups and tags!");
        }
        yield ResourceType.STUDENT;
      }
      case "teachers" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.TAG
            && currentType != ResourceType.SUBJECT) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters), subjects and tags!");
        }
        yield ResourceType.TEACHER;
      }
      case "studentGroups" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.STUDENT
            && currentType != ResourceType.GRADE && currentType != ResourceType.TAG) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters), students,"
                  + " grades and tags!");
        }
        yield ResourceType.STUDENT_GROUP;
      }
      case "rooms" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.TAG) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters) and tags!");
        }
        yield ResourceType.ROOM;
      }
      case "subjects" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.TEACHER
            && currentType != ResourceType.TAG) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters), teachers and tags!");
        }
        yield ResourceType.SUBJECT;
      }
      case "grades" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.TAG) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters) and tags!");
        }
        yield ResourceType.GRADE;
      }
      case "timeslots" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.TAG) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters) and tags!");
        }
        yield ResourceType.TIMESLOT;
      }
      case "lessons" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.TEACHER
            && currentType != ResourceType.STUDENT_GROUP && currentType != ResourceType.ROOM
            && currentType != ResourceType.SUBJECT && currentType != ResourceType.TIMESLOT) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters), teachers, studentGroups,"
                  + " rooms, subjects and timeslots!");
        }
        yield ResourceType.LESSON;
      }
      case "tags" -> {
        if (currentType != ResourceType.TIMETABLE && currentType != ResourceType.STUDENT
            && currentType != ResourceType.TEACHER && currentType != ResourceType.STUDENT_GROUP
            && currentType != ResourceType.ROOM && currentType != ResourceType.SUBJECT
            && currentType != ResourceType.GRADE && currentType != ResourceType.TIMESLOT) {
          throw new ParseException(
              "Attribute \"" + attribute
                  + "\" is only available for \"this\"(outside filters), students, teachers, "
                  + "studentGroups, rooms, subjects grades and timeslots!");
        }
        yield ResourceType.TAG;
      }
      default -> throw new ParseException("Illegal attribute name: " + attribute);
    };
  }

}
