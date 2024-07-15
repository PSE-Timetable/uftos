package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ucdl.UcdlToken;
import java.util.List;

/**
 * The abstract syntax tree containing an operator.
 */
public record OperatorDto(UcdlToken token, List<AbstractSyntaxTreeDto> parameters)
    implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return token;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.token);
    stringBuilder.append("{\n");

    for (AbstractSyntaxTreeDto ast : this.parameters) {
      stringBuilder.append(ast.toString());
      stringBuilder.append("\n");
    }

    stringBuilder.append("}");

    return stringBuilder.toString();
  }
}
