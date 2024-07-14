package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.UcdlToken;
import java.util.List;

/**
 * The abstract syntax tree containing an element.
 */
public record ElementDto(UcdlToken token, AbstractSyntaxTreeDto name,
                         List<AbstractSyntaxTreeDto> attributes, ResourceType type)
    implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return token;
  }
}
