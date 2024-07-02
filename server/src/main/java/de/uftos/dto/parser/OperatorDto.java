package de.uftos.dto.parser;

/**
 * The abstract syntax tree containing an operator.
 */
public record OperatorDto(SemanticToken token, AbstractSyntaxTreeDto param1, AbstractSyntaxTreeDto param2) implements AbstractSyntaxTreeDto {

  @Override
  public SemanticToken getToken() {
    return this.token;
  }

  @Override
  public String toString() {
    return this.getToken() +
        ";{" +
        this.param1 +
        "};{" +
        this.param2 +
        "}";
  }
}
