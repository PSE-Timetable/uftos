package de.uftos.dto.solver;

import java.util.List;

/**
 * An instance of a constraint definition.
 *
 * @param definitionName the name of the constraint definition instantiated.
 * @param type           the type defining, whether the constraint instance is hard/soft rewarded/penalized.
 * @param parameters     the parameters used in the instance.
 */
public record ConstraintInstanceDto(
    String definitionName,
    RewardPenalize type,
    List<ResourceProblemDto> parameters
) {
}
