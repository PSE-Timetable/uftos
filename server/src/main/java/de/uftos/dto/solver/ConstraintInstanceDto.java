package de.uftos.dto.solver;

import java.util.List;

/**
 * An instance of a constraint definition.
 *
 * @param definitionName the name of the constraint definition instantiated.
 * @param parameters     the parameters used in the instance.
 */
public record ConstraintInstanceDto(
    String definitionName,
    List<ResourceProblemDto> parameters
) {
}
