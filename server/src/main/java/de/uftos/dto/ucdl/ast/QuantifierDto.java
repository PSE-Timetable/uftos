package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ucdl.UcdlToken;

/**
 * The abstract syntax tree containing a boolean quantifier.
 */
public record QuantifierDto(UcdlToken token, AbstractSyntaxTreeDto elements,
                            AbstractSyntaxTreeDto body) implements AbstractSyntaxTreeDto {
  @Override
  public UcdlToken getToken() {
    return token;
  }

  @Override
  public String toString() {

    return this.token
        + "("
        + this.elements.toString()
        + ") {\n"
        + this.body.toString()
        + "}";
  }
}
