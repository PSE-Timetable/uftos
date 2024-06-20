package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

public record ValueDto<T>(T value) implements AbstractSyntaxTreeDto {
  //todo:
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
