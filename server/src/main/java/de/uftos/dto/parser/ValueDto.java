package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

/**
 * The abstract syntax tree containing a value of type T.
 */
public record ValueDto<T>(T value) implements AbstractSyntaxTreeDto {
  //todo:
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
