package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.parser.AbstractSyntaxTreeDto;
import de.uftos.dto.parser.ValueDto;

public class DefinitionParser {
  public static AbstractSyntaxTreeDto parseDefinition(String definition) {
    return new ValueDto<String>(definition);
  }
}
