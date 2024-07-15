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

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.token);
    stringBuilder.append("(");
    stringBuilder.append(this.name.toString());
    stringBuilder.append(") {\n");

    for (AbstractSyntaxTreeDto ast : this.attributes) {
      stringBuilder.append(ast.toString());
      stringBuilder.append("\n");
    }

    stringBuilder.append("}");

    return stringBuilder.toString();
  }
}
