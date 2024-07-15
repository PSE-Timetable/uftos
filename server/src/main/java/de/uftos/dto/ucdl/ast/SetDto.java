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

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.token);
    stringBuilder.append("(");
    stringBuilder.append(this.setName.toString());
    stringBuilder.append(") {\n");

    for (AbstractSyntaxTreeDto ast : this.modifiers) {
      stringBuilder.append(ast.toString());
      stringBuilder.append("\n");
    }

    stringBuilder.append("}");

    return stringBuilder.toString();
  }
}
