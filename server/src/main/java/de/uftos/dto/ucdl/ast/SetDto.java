package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.UcdlToken;
import java.util.List;

/**
 * The abstract syntax tree containing a set.
 */
public record SetDto(UcdlToken token, ResourceType type, AbstractSyntaxTreeDto setName,
                     List<AbstractSyntaxTreeDto> modifiers) implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return token;
  }
}
