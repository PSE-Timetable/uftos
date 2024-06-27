package de.uftos.ucdl;

/**
 * Token which can be parsed from a correct UCDL-file for the definition of a constraint.
 */
public enum UcdlToken {
  CODEBLOCK,
  FOR,
  IF,
  RETURN,
  AND,
  OR,
  NOT,
  IMPLIES,
  FOR_ALL,
  EXISTS,
  IN,
  EQUALS,
  NOT_EQUALS,
  SMALLER_EQUALS,
  GREATER_EQUALS,
  SMALLER,
  GREATER,
  SET,
  ATTRIBUTE,
  ELEMENT,
  FLATMAP,
  FILTER,
  NUMBER_SET,
  RESOURCE_SET,
  NUMBER,
  RESOURCE
}
