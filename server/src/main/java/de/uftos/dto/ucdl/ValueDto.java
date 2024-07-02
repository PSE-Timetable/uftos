package de.uftos.dto.ucdl;

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
