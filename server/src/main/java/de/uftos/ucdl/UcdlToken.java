package de.uftos.ucdl;

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
  AND, //-> operatordto
  OR, //-> operatordto
  NOT, //-> operatordto
  IMPLIES, //-> operatordto
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
  FLATMAP, //-> operatordto
  FILTER, //-> operatordto
  RESOURCE_SET, //-> valuedto
  NUMBER_SET, //-> valuedto
  ATTRIBUTE, //-> valuedto
  ELEMENT, //-> valuedto
  NUMBER, //-> valuedto
  RESOURCE, //-> valuedto
  PARAMETER, //-> valuedto
  VARIABLE //-> valuedto
}
