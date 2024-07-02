package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

/**
 * The abstract syntax tree containing a boolean quantifier.
 */
public record QuantifierDto() implements AbstractSyntaxTreeDto {
  //todo:
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
