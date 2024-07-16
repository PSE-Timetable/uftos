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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: !!!REFACTOR!!!

/**
 * This class parses the ucdl-code of the "definition"-field of a constraint definition and returns
 * the abstract syntax tree, which resembles the code.
 */
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
                                                      HashMap<String, ResourceType> parameters)
      throws ParseException {
    parameters.put("this", ResourceType.TIMETABLE);
    SimpleNode root = SyntaxChecker.parseString(definition);
    return buildAst(root, parameters);
  }

  private static AbstractSyntaxTreeDto buildAst(Node root, HashMap<String, ResourceType> parameters)
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
        boolean returnValue =
            getReturnValue(params); //returned value is irrelevant, only error checking
        return new OperatorDto(UcdlToken.CODEBLOCK, params);
      }
      case "RETURN" -> {
        SimpleNode boolVal = (SimpleNode) root.jjtGetChild(0);
        return switch (boolVal.jjtGetFirstToken().image) {
          case "true" -> new ValueDto<>(UcdlToken.RETURN, true);
          case "false" -> new ValueDto<>(UcdlToken.RETURN, false);
          default -> throw new ParseException(
              "Illegal return value! \"true\" or \"false\" expected!");
        };
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
        boolean returnValue = false;

        AbstractSyntaxTreeDto bool = buildAst(root.jjtGetChild(0), parameters);

        List<AbstractSyntaxTreeDto> body = new ArrayList<>();

        Node bodyElements = root.jjtGetChild(1);

        switch (bodyElements.jjtGetChild(0).toString()) {
          case "RETURN":
            ValueDto<Boolean> dto =
                (ValueDto<Boolean>) buildAst(bodyElements.jjtGetChild(0), parameters);
            returnValue = dto.value();
            body.add(dto);
            break;
          case "CONTROLSEQUENCE":
            body.add(buildAst(bodyElements.jjtGetChild(0), parameters)); //first control sequence

            Node controlSequenceList = bodyElements.jjtGetChild(1); //rest of the control sequences
            while (controlSequenceList.jjtGetNumChildren() == 2) {
              body.add(buildAst(controlSequenceList.jjtGetChild(0), parameters));
              controlSequenceList = controlSequenceList.jjtGetChild(1);
            }
            //semantic check for legal return values
            returnValue = getReturnValue(body);
            break;
          default:
            throw new IllegalStateException();
        }
        return new ControlSequenceDto(UcdlToken.IF, bool, body, returnValue);
      }
      case "FORALL" -> {
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
        AbstractSyntaxTreeDto body = buildAst(root.jjtGetChild(1), parameters);
        parameters.remove(varName.value()); //removing the new variable

        return new QuantifierDto(UcdlToken.FOR_ALL, variable, body);
      }
      case "EXISTS" -> {
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
        AbstractSyntaxTreeDto body = buildAst(root.jjtGetChild(1), parameters);
        parameters.remove(varName.value()); //removing the new variable

        return new QuantifierDto(UcdlToken.EXISTS, variable, body);
      }
      case "IMPLIES" -> {
        AbstractSyntaxTreeDto firstArgument = buildAst(root.jjtGetChild(0), parameters);
        if (root.jjtGetChild(1).jjtGetNumChildren() > 0) {
          AbstractSyntaxTreeDto secondArgument =
              buildAst(root.jjtGetChild(1).jjtGetChild(0), parameters);
          List<AbstractSyntaxTreeDto> params = new ArrayList<>();
          params.add(firstArgument);
          params.add(secondArgument);
          return new OperatorDto(UcdlToken.IMPLIES, params);
        }
        return firstArgument;
      }
      case "OR" -> {
        AbstractSyntaxTreeDto firstArgument = buildAst(root.jjtGetChild(0), parameters);
        if (root.jjtGetChild(1).jjtGetNumChildren() > 0) {
          AbstractSyntaxTreeDto secondArgument =
              buildAst(root.jjtGetChild(1).jjtGetChild(0), parameters);
          List<AbstractSyntaxTreeDto> params = new ArrayList<>();
          params.add(firstArgument);
          params.add(secondArgument);
          return new OperatorDto(UcdlToken.OR, params);
        }
        return firstArgument;
      }
      case "AND" -> {
        AbstractSyntaxTreeDto firstArgument = buildAst(root.jjtGetChild(0), parameters);
        if (root.jjtGetChild(1).jjtGetNumChildren() > 0) {
          AbstractSyntaxTreeDto secondArgument =
              buildAst(root.jjtGetChild(1).jjtGetChild(0), parameters);
          List<AbstractSyntaxTreeDto> params = new ArrayList<>();
          params.add(firstArgument);
          params.add(secondArgument);
          return new OperatorDto(UcdlToken.AND, params);
        }
        return firstArgument;
      }
      case "NOT" -> {
        if (((SimpleNode) root).jjtGetFirstToken().image.equals("NOT")) {
          List<AbstractSyntaxTreeDto> param = new ArrayList<>();
          param.add(buildAst(root.jjtGetChild(0), parameters));
          return new OperatorDto(UcdlToken.NOT, param);
        }
        return buildAst(root.jjtGetChild(0), parameters);
      }
      case "ISEMPTY" -> {
        List<AbstractSyntaxTreeDto> param = new ArrayList<>();
        param.add(buildAst(root.jjtGetChild(0), parameters));
        return new OperatorDto(UcdlToken.IS_EMPTY, param);
      }
      case "ELEMENTINSETOREQUATION" -> {
        List<AbstractSyntaxTreeDto> params = new ArrayList<>();
        params.add(buildAst(root.jjtGetChild(0), parameters));

        Node secondPart = root.jjtGetChild(1);

        if (secondPart.toString().equals("ELEMENTINSET")) {
          params.add(buildAst(secondPart.jjtGetChild(0), parameters));
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
      case "ELEMENTNAME" -> {
        String image = ((SimpleNode) root).jjtGetFirstToken().image;
        if (!parameters.containsKey(image)) {
          throw new ParseException("Parameter/Variable \"" + image + "\" does not exist!");
        }
        return new ValueDto<>(UcdlToken.VALUE_REFERENCE, image);
      }
      case "NUMBERELEMENT" -> {
        if (((SimpleNode) root).jjtGetFirstToken().image.equals("size")) {
          List<AbstractSyntaxTreeDto> set = new ArrayList<>();
          set.add(buildAst(root.jjtGetChild(0), parameters));
          return new OperatorDto(UcdlToken.SIZE, set);
        } else {
          return new ValueDto<>(UcdlToken.NUMBER, Integer.parseInt(
              ((SimpleNode) root).jjtGetFirstToken().image));
        }
      }
      case "SET" -> {
        AbstractSyntaxTreeDto setName = buildAst(root.jjtGetChild(0).jjtGetChild(0), parameters);

        ResourceType setType;

        if (setName.getToken() == UcdlToken.NUMBER_SET) {
          setType = ResourceType.NUMBER;
        } else {
          setType = ((ElementDto) setName).type();
        }

        List<AbstractSyntaxTreeDto> modifiers = new ArrayList<>();

        Node modifierList = root.jjtGetChild(1);
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
              if ((filter.getToken() == UcdlToken.NUMBER_SET && setType != ResourceType.NUMBER)
                  || (filter.getToken() == UcdlToken.RESOURCE_SET
                  && ((SetDto) filter).type() != setType)) {
                throw new ParseException("Sets can only be filtered using sets of the same type!");
              }
            }

            modifiers.add(new OperatorDto(UcdlToken.FILTER, filters));
            modifierList = modifierList.jjtGetChild(2);
            parameters.replace("this", thisType);
          }
        }

        return new SetDto(UcdlToken.RESOURCE_SET, setType, setName, modifiers);
      }
      case "NUMBERSET" -> {
        List<Integer> values = new ArrayList<>();

        values.add(Integer.parseInt(((SimpleNode) root).jjtGetFirstToken().next.image));

        SimpleNode numberList = (SimpleNode) root.jjtGetChild(0);
        while (numberList.jjtGetNumChildren() > 0) {
          values.add(Integer.parseInt(numberList.jjtGetFirstToken().next.image));
          numberList = (SimpleNode) numberList.jjtGetChild(0);
        }
        return new ValueDto<>(UcdlToken.NUMBER_SET, values.toArray(new Integer[0]));
      }
      case "VALUEREFERENCE" -> {
        return new ValueDto<>(UcdlToken.VALUE_REFERENCE,
            ((SimpleNode) root).jjtGetFirstToken().image);
      }
      case "FILTER" -> {
        switch (root.jjtGetChild(0).toString()) {
          case "BOOLVALUE":
          case "FORALL":
          case "EXISTS":
          case "ISEMPTY":
            return buildAst(root.jjtGetChild(0), parameters);
          case "NUMBERSET":
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
                        "ets can only be filtered using sets of the same type!");
                  }
                }
                modifiers.add(new OperatorDto(UcdlToken.FILTER, filters));
              }
            }
            parameters.replace("this", thisType);
            return new SetDto(UcdlToken.RESOURCE_SET, ResourceType.NUMBER, numberSet, modifiers);
          case "ELEMENT":
            switch (root.jjtGetChild(1).toString()) {
              case "SETMODIFICATION":
                AbstractSyntaxTreeDto setName = buildAst(root.jjtGetChild(0), parameters);
                ResourceType setType;
                if (setName.getToken() == UcdlToken.NUMBER) {
                  setType = ResourceType.NUMBER;
                } else {
                  setType = ((ElementDto) setName).type();
                }

                modifiers = new ArrayList<>();

                modifierList = root.jjtGetChild(1);
                while (modifierList.jjtGetNumChildren() > 0) {
                  if (modifierList.jjtGetNumChildren() == 2) {
                    SimpleNode flatmap = (SimpleNode) modifierList.jjtGetChild(0);
                    String attribute = flatmap.jjtGetFirstToken().image;
                    setType = getNextResourceType(setType, attribute);
                    modifiers.add(new ValueDto<>(UcdlToken.ATTRIBUTE, attribute));
                    modifierList = modifierList.jjtGetChild(1);

                  } else if (modifierList.jjtGetNumChildren() == 3) {
                    thisType = parameters.get("this");
                    parameters.replace("this", setType);

                    List<AbstractSyntaxTreeDto> filters = new ArrayList<>();
                    filters.add(buildAst(modifierList.jjtGetChild(0), parameters));

                    Node filterList = modifierList.jjtGetChild(1);
                    while (filterList.jjtGetNumChildren() > 0) {
                      filters.add(buildAst(filterList.jjtGetChild(0), parameters));
                      filterList = filterList.jjtGetChild(1);
                    }

                    for (AbstractSyntaxTreeDto filter : filters) {
                      if ((filter.getToken() == UcdlToken.NUMBER_SET
                          && setType != ResourceType.NUMBER)
                          || (filter.getToken() == UcdlToken.RESOURCE_SET
                          && ((SetDto) filter).type() != setType)) {
                        throw new ParseException(
                            "Sets can only be filtered using sets of the same type!");
                      }
                    }

                    modifiers.add(new OperatorDto(UcdlToken.FILTER, filters));
                    modifierList = modifierList.jjtGetChild(2);
                    parameters.replace("this", thisType);
                  }
                }

                return new SetDto(UcdlToken.RESOURCE_SET, setType, setName, modifiers);

              case "ELEMENTINSET":
                ElementDto element = (ElementDto) buildAst(root.jjtGetChild(0), parameters);
                SetDto set = (SetDto) buildAst(root.jjtGetChild(1).jjtGetChild(0), parameters);

                if (element.type() != set.type()) {
                  throw new ParseException("Elements can only be in Sets of the same type!");
                }

                List<AbstractSyntaxTreeDto> params = new ArrayList<>();
                params.add(element);
                params.add(set);

                AbstractSyntaxTreeDto elementInSet = new OperatorDto(UcdlToken.IN, params);

                if (root.jjtGetNumChildren() == 2) {
                  return elementInSet;
                }
                params = new ArrayList<>();
                params.add(elementInSet);
                params.add(buildAst(root.jjtGetChild(2).jjtGetChild(0), parameters));
                return switch (((SimpleNode) root.jjtGetChild(2)).jjtGetFirstToken().image) {
                  case "implies", "->" -> new OperatorDto(UcdlToken.IMPLIES, params);
                  case "or", "||" -> new OperatorDto(UcdlToken.OR, params);
                  case "and", "&&" -> new OperatorDto(UcdlToken.AND, params);
                  default -> throw new IllegalStateException();
                };
              case "EQUATION":
                params = new ArrayList<>();
                Node firstElement = root.jjtGetChild(0);
                Node secondElement = root.jjtGetChild(1).jjtGetChild(1);
                String comparator =
                    ((SimpleNode) root.jjtGetChild(1).jjtGetChild(0)).jjtGetFirstToken().image;

                params.add(buildAst(firstElement, parameters));
                params.add(buildAst(secondElement, parameters));
                UcdlToken token = getComparatorToken(comparator, params);
                return new OperatorDto(token, params);
              default:
                throw new IllegalStateException();
            }
          default:
            throw new IllegalStateException();

        }
      }
      //includes cases: "CONTROLSEQUENCELIST", "CONTROLSEQUENCERETURN",
      // "OPTIONALIMPLIES", "OPTIONALOR", "OPTIONALAND",
      // "ELEMENTINSET", "EQUATION", "ELEMENTEQUATION", "ELEMENTATTRIBUTELIST",
      // "SETNAME", "SETMODIFICATION", "NUMBERLIST", "ATTRIBUTE", "FILTERLIST"
      default -> throw new IllegalStateException();
    }
  }

  //checks that all returnValues are the same and returns the value
  // (throws an exception if true and false are returnValues inside the body)
  private static boolean getReturnValue(List<AbstractSyntaxTreeDto> body) throws ParseException {
    boolean and = true;
    boolean or = false;
    for (AbstractSyntaxTreeDto ast : body) {
      if (ast.getToken() == UcdlToken.FOR || ast.getToken() == UcdlToken.IF) {
        ControlSequenceDto cs = (ControlSequenceDto) ast;
        and = and && cs.returnValue();
        or = or || cs.returnValue();
      }
    }
    if (and != or) {
      throw new ParseException(
          "All control sequences in the body of a control sequence need to return"
              + " the same boolean value!");
    }
    return and;
  }

  private static UcdlToken getComparatorToken(String comparator, List<AbstractSyntaxTreeDto> params)
      throws ParseException {
    UcdlToken token = switch (comparator) {
      case ">" -> UcdlToken.GREATER;
      case "<" -> UcdlToken.SMALLER;
      case ">=" -> UcdlToken.GREATER_EQUALS;
      case "<=" -> UcdlToken.SMALLER_EQUALS;
      case "=", "==" -> UcdlToken.EQUALS;
      case "!=" -> UcdlToken.NOT_EQUALS;
      default -> throw new IllegalStateException();
    };
    if (token == UcdlToken.GREATER || token == UcdlToken.SMALLER
        || token == UcdlToken.GREATER_EQUALS || token == UcdlToken.SMALLER_EQUALS) {
      for (AbstractSyntaxTreeDto ast : params) {

        if (ast.getToken() != UcdlToken.NUMBER && ast.getToken() != UcdlToken.SIZE
            && !(ast.getToken() == UcdlToken.ELEMENT
            && ((ElementDto) ast).type() == ResourceType.NUMBER)) {
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