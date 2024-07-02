package de.uftos.dto.ucdl;

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
