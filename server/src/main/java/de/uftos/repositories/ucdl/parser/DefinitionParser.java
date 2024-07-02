package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.ucdl.AbstractSyntaxTreeDto;
import de.uftos.dto.ucdl.ValueDto;

public class DefinitionParser {
  public static AbstractSyntaxTreeDto parseDefinition(String definition) {
    return new ValueDto<String>(definition);
  }
}
