package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.UcdlToken;
import java.util.List;

public record SetDto(UcdlToken token, ResourceType type, AbstractSyntaxTreeDto setName,
                     List<AbstractSyntaxTreeDto> modifiers) implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return token;
  }
}
