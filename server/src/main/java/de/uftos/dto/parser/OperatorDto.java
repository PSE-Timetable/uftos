package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

/**
 * The abstract syntax tree containing an operator.
 */
public record OperatorDto() implements AbstractSyntaxTreeDto {
  //todo:
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
