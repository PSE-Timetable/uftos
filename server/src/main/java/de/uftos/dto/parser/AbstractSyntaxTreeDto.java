package de.uftos.dto.parser;

import de.uftos.ucdl.UcdlToken;

/**
 * The abstract syntax tree describing the structure of a constraint definition.
 */
public interface AbstractSyntaxTreeDto {
  UcdlToken getToken();
}
