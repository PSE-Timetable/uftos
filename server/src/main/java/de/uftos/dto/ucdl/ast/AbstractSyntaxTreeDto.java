package de.uftos.dto.ucdl.ast;

import de.uftos.dto.ucdl.UcdlToken;

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
