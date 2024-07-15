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

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.token);
    stringBuilder.append("(");
    stringBuilder.append(this.parenthesesContent.toString());
    stringBuilder.append(") {\n");

    for (AbstractSyntaxTreeDto ast : this.body) {
      stringBuilder.append(ast.toString());
      stringBuilder.append("\n");
    }

    stringBuilder.append("}");

    return stringBuilder.toString();
  }
}
