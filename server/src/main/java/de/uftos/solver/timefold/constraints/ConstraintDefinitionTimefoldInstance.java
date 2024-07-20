package de.uftos.solver.timefold.constraints;

import de.uftos.dto.solver.RewardPenalize;
import de.uftos.solver.timefold.domain.ResourceTimefoldInstance;
import java.util.List;
import java.util.function.Function;

public record ConstraintDefinitionTimefoldInstance(
    String name,
    RewardPenalize defaultType,
    Function<List<ResourceTimefoldInstance>, Boolean> evaluationFunction
) {
}