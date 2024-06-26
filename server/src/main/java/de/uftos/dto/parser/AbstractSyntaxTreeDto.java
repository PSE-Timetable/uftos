package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

/**
 * The abstract syntax tree describing the structure of a constraint definition.
 */
public interface AbstractSyntaxTreeDto {
  /**
   * The token of the abstract syntax tree describing the operation of the tree.
   *
   * @return the token of the abstract syntax tree.
   */
  UcdlToken getToken();
}
