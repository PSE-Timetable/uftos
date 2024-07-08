package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ucdl.UcdlToken;
import java.util.List;

public record FilterDto(UcdlToken token, List<AbstractSyntaxTreeDto> filters)
    implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return null;
  }
}
