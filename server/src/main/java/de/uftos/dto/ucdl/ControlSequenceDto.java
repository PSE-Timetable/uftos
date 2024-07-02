package de.uftos.dto.ucdl;

/**
 * The abstract syntax tree containing a control sequence.
 */
public record ControlSequenceDto() implements AbstractSyntaxTreeDto {
  //todo:
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
