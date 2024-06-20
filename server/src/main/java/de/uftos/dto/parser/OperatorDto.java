package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

public record OperatorDto() implements AbstractSyntaxTreeDto {
  //todo:
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
