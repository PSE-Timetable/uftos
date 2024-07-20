package de.uftos.solver.timefold.constraints;

import de.uftos.dto.solver.ConstraintInstanceDto;
import java.util.HashMap;

/**
 * This factory creates constraint instances for use with the Timefold-solver
 * based on ConstraintInstanceDtos.
 */
public class ConstraintInstanceFactory {

  /**
   * Creates a constraint definition based on a ConstraintDefinitionDto.
   *
   * @param instance the ConstraintInstanceDto.
   * @return a ConstraintDefinitionTimefoldInstance representing the given ConstraintDefinitionDto.
   */
  public static ConstraintDefinitionTimefoldInstance getConstraintDefinition(
      ConstraintInstanceDto instance,
      HashMap<String, ConstraintDefinitionTimefoldInstance> definitions
  ) {

    return null;
  }
}
