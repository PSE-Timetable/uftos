package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ucdl.UcdlToken;

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
