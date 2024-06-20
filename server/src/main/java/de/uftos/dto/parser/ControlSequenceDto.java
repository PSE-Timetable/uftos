package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

public record ControlSequenceDto() implements AbstractSyntaxTreeDto {
  //todo:
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
