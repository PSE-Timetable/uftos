package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ucdl.UcdlToken;

/**
 * The abstract syntax tree containing a value of type T.
 */
public record ValueDto<T>(UcdlToken token, T value) implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return token;
  }
}
