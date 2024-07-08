package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.UcdlToken;
import java.util.List;

public record ElementDto(UcdlToken token, AbstractSyntaxTreeDto name,
                         List<AbstractSyntaxTreeDto> attributes, ResourceType type)
    implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
