package de.uftos.solver.timefold.constraints;

import de.uftos.dto.solver.RewardPenalize;
import de.uftos.solver.timefold.domain.ResourceTimefoldInstance;
import java.util.List;
import java.util.function.Function;

/**
 * This class models a constraint definition, which can be used by the Timefold solver.
 *
 * @param name               the name of the constraint definition.
 * @param defaultType        the default hard/soft penalize/reward value for this constraint definition.
 * @param evaluationFunction the function, which is used to evaluate this constraint.
 */
public record ConstraintDefinitionTimefoldInstance(
    String name,
    RewardPenalize defaultType,
    Function<List<ResourceTimefoldInstance>, Boolean> evaluationFunction
) {
}