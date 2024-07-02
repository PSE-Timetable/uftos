package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ucdl.UcdlToken;

/**
 * The abstract syntax tree containing a control sequence.
 */
public record ControlSequenceDto(UcdlToken token, AbstractSyntaxTreeDto param, AbstractSyntaxTreeDto body) implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return token;
  }
}
