package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.ucdl.UcdlToken;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.repositories.ucdl.parser.javacc.SyntaxChecker;

public class DefinitionParser {
  public static AbstractSyntaxTreeDto parseDefinition(String definition) {
    System.out.println(SyntaxChecker.parseString(definition));
    return new ValueDto<String>(UcdlToken.RESOURCE, definition);
  }
}
