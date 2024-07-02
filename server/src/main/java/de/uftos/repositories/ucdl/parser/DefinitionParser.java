package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.parser.AbstractSyntaxTreeDto;
import de.uftos.dto.parser.SemanticToken;
import de.uftos.dto.parser.ValueDto;

public class DefinitionParser {
  private static Lexer lexer;

  public static AbstractSyntaxTreeDto parseDefinition(String definition) {
    lexer = new Lexer(definition);

    AbstractSyntaxTreeDto ast;
    switch (lexer.current.ucdlToken) {
      case LCURLY:
        lexer.lex();
      case FOR:
      case IF:
        ast = parseCodeblock();
        break;
      case AND:
      case OR:
      case NOT:
      case IMPLIES:
      case FORALL:
      case EXISTS:
        ast = parseBool();
      default:
        System.out.println("nothing happened");
    }

    if (lexer.current.ucdlToken != UcdlToken.END_OF_FILE) {
      throw new IllegalArgumentException();
    }


    return new ValueDto<>(SemanticToken.RESOURCE, definition);
  }


  private static AbstractSyntaxTreeDto parseCodeblock() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseFor() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseIf() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseBool() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseForall() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseExists() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseImplies() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseOr() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseAnd() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseNot() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseEquation() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseComparison() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseFlatmap() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseSet() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseElement() {
    return null;
  }

  private static AbstractSyntaxTreeDto parseFilter() {
    return null;
  }

}
