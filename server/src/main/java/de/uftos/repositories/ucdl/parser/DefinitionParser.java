package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.UcdlToken;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.dto.ucdl.ast.ControlSequenceDto;
import de.uftos.dto.ucdl.ast.OperatorDto;
import de.uftos.dto.ucdl.ast.SetDto;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.repositories.ucdl.parser.javacc.Node;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import de.uftos.repositories.ucdl.parser.javacc.SimpleNode;
import de.uftos.repositories.ucdl.parser.javacc.SyntaxChecker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefinitionParser {
  public static AbstractSyntaxTreeDto parseDefinition(String definition,
                                                      HashMap<String, ResourceType> parameters)
      throws ParseException {
    SimpleNode root = SyntaxChecker.parseString(definition);
    return buildAst(root, parameters);
  }

  private static AbstractSyntaxTreeDto buildAst(Node root, HashMap<String, ResourceType> parameters)
      throws ParseException {
    System.out.println(root + ":" + root.jjtGetNumChildren());
    String s = root.toString();
    switch (s) {
      case "START", "BOOL", "DEFINITION", "CONTROLSEQUENCE" -> {
        return buildAst(root.jjtGetChild(0), parameters);
      }
      case "BOOLVALUE" -> {
        return null;
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
        boolean and = true;
        boolean or = false;
        for (AbstractSyntaxTreeDto ast : params) {
          if (ast.getToken() == UcdlToken.FOR || ast.getToken() == UcdlToken.IF) {
            ControlSequenceDto cs = (ControlSequenceDto) ast;
            and = and && cs.returnValue();
            or = or || cs.returnValue();
          }
        }
        if (and != or) {
          throw new ParseException(
              "All control sequences in the body of a control sequence need to return the same boolean value!");
        }
        return new OperatorDto(UcdlToken.CODEBLOCK, params);
      }
      case "CONTROLSEQUENCELIST" -> {
        return null;
      }
      case "RETURN" -> {
        SimpleNode boolVal = (SimpleNode) root.jjtGetChild(0);
        switch (boolVal.jjtGetFirstToken().image) {
          case "true":
            return new ValueDto<>(UcdlToken.RETURN, true);
          case "false":
            return new ValueDto<>(UcdlToken.RETURN, false);
          default:
            throw new ParseException(
                "Illegal return value! \"true\" or \"false\" expected!");
        }
      }
      case "FOR" -> {
        List<AbstractSyntaxTreeDto> variableDefinition = new ArrayList<>();
        ValueDto<String> varName = (ValueDto<String>) buildAst(root.jjtGetChild(0), parameters);
        variableDefinition.add(varName); //variable name
        if (parameters.containsKey(varName.value())) {
          throw new ParseException("Variable name \"" + varName.value() +
              "\" does already exist inside this namespace!");
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
              "All control sequences in the body of a control sequence need to return the same boolean value!");
        }
        return new ControlSequenceDto(UcdlToken.FOR, variable, body, and);
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

              //semantic check for legal return values
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
                    "All control sequences in the body of a control sequence need to return the same boolean value!");
              }
              returnValue = and;
            }
            break;
          default:
            throw new IllegalStateException();
        }
        return new ControlSequenceDto(UcdlToken.IF, bool, body, returnValue);
      }
      case "CONTROLSEQUENCERETURN" -> {
        throw new ParseException("");
      }
      case "FORALL" -> {
        return null;
      }
      case "EXISTS" -> {
        return null;
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
      case "OPTIONALIMPLIES" -> {
        return null;
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
      case "OPTIONALOR" -> {
        return null;
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
      case "OPTIONALAND" -> {
        return null;
      }
      case "NOT" -> {
        return null;
      }
      case "ISEMPTY" -> {
        return null;
      }
      case "ELEMENTINSETOREQUATION" -> {
        return null;
      }
      case "ELEMENTINSET" -> {
        return null;
      }
      case "EQUATION" -> {
        return null;
      }
      case "ELEMENTEQUATION" -> {
        return null;
      }
      case "ELEMENT" -> {
        return null;
      }
      case "ELEMENTNAME" -> {
        return null;
      }
      case "ELEMENTATTRIBUTELIST" -> {
        return null;
      }
      case "NUMBERELEMENT" -> {
        if (((SimpleNode) root).jjtGetFirstToken().image.equals("SIZE")) {
          List<AbstractSyntaxTreeDto> set = new ArrayList<>();
          set.add(buildAst(root.jjtGetChild(0), parameters));
          return new OperatorDto(UcdlToken.SIZE, set);
        } else {
          return new ValueDto<>(UcdlToken.NUMBER, Integer.parseInt(
              ((SimpleNode) root).jjtGetFirstToken().image));
        }
      }
      case "SET" -> {
        return new SetDto(UcdlToken.RESOURCE_SET, ResourceType.TEACHER);
      }
      case "SETNAME" -> {
        return null;
      }
      case "SETMODIFICATION" -> {
        return null;
      }
      case "NUMBERSET" -> {
        return null;
      }
      case "NUMBERLIST" -> {
        return null;
      }
      case "ATTRIBUTE" -> {
        return null;
      }
      case "VALUEREFERENCE" -> {
        return new ValueDto<>(UcdlToken.VALUE_REFERENCE,
            ((SimpleNode) root).jjtGetFirstToken().image);
      }
      case "FILTER" -> {
        return null;
      }
      case "FILTERLIST" -> {
        return null;
      }
      default -> {
        throw new IllegalStateException();
      }
    }
  }
}