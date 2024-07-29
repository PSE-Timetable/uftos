package de.uftos.dto.requestdtos;

import de.uftos.entities.ConstraintArgument;
import jakarta.validation.constraints.NotEmpty;

/**
 * A data transfer object used in the constraint HTTP requests.
 *
 * @param parameterName the name of the parameter that this argument instances.
 * @param argumentId    the id ot the entity that this argument references.
 */
public record ConstraintArgumentRequestDto(@NotEmpty String parameterName,
                                           @NotEmpty String argumentId) {
  /**
   * Maps the information from the data transfer object to a new constraint argument entity.
   *
   * @return the new constraint argument entity.
   */
  public ConstraintArgument map() {
    return new ConstraintArgument(parameterName, argumentId);
  }
}
