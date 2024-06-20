package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

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
