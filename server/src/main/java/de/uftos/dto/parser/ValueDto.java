package de.uftos.dto.parser;

/**
 * The abstract syntax tree containing a value of type T.
 */
public record ValueDto<T>(SemanticToken token, T value) implements AbstractSyntaxTreeDto {

  @Override
  public SemanticToken getToken() {
    return this.token;
  }

  @Override
  public String toString() {
    return this.token + ";" + this.value;
  }
}
