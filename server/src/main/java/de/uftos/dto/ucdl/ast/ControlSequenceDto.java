package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ucdl.UcdlToken;
import java.util.List;

/**
 * The abstract syntax tree containing a control sequence.
 */
public record ControlSequenceDto(UcdlToken token, AbstractSyntaxTreeDto parenthesesContent,
                                 List<AbstractSyntaxTreeDto> body, boolean returnValue)
    implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return token;
  }
}
