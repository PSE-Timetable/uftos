package de.uftos.repositories.solver.timefold.constraints.constraintinstances;

import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import java.util.List;
import java.util.function.Function;

/**
 * This class models a constraint instance, which can be used by the Timefold solver.
 * This instance is soft rewarded when the evaluation function returns true.
 *
 * @param parameters         the resources used in this constraint instance.
 * @param evaluationFunction the function used to evaluate this constraint instance.
 */
public record ConstraintInstanceSoftReward(
    List<ResourceTimefoldInstance> parameters,
    Function<List<ResourceTimefoldInstance>, Boolean> evaluationFunction
) implements ConstraintInstanceTimefoldInstance {
  public boolean evaluate() {
    return evaluationFunction.apply(parameters);
  }
}
