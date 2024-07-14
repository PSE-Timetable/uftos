package de.uftos.dto.ucdl;

/**
 * Token which can be parsed from a correct UCDL-file for the definition of a constraint.
 */
public enum UcdlToken {

  //control sequences
  CODEBLOCK, //-> operatordto
  FOR, //-> controlsequencedto
  IF, //-> controlsequencedto
  RETURN, //-> valuedto

  //bool
  IMPLIES, //-> operatordto
  OR, //-> operatordto
  AND, //-> operatordto
  NOT, //-> operatordto
  FOR_ALL, //-> quantifierdto
  EXISTS, //-> quantifierdto
  BOOL_VALUE, //-> valuedto

  //comparisons
  EQUALS, //-> operatordto
  NOT_EQUALS, //-> operatordto
  SMALLER_EQUALS, //-> operatordto
  GREATER_EQUALS, //-> operatordto
  SMALLER, //-> operatordto
  GREATER, //-> operatordto

  //sets and elements
  IN, //-> operatordto
  OF, //-> operatordto
  FILTER, //-> operatordto
  IS_EMPTY, //-> operatordto
  SIZE, //-> operatordto
  RESOURCE_SET, //-> valuedto
  NUMBER_SET, //-> valuedto
  ATTRIBUTE, //-> valuedto
  ELEMENT, //-> valuedto
  NUMBER, //-> valuedto
  VALUE_REFERENCE //-> valuedto
}
