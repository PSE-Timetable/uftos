package de.uftos.solver.timefold.constraints.constraintinstances;

import de.uftos.solver.timefold.domain.ResourceTimefoldInstance;
import java.util.List;
import java.util.function.Function;

/**
 * This class models a constraint instance, which can be used by the Timefold solver.
 * This instance is hard rewarded when the evaluation function returns true.
 *
 * @param parameters         the resources used in this constraint instance.
 * @param evaluationFunction the function used to evaluate this constraint instance.
 */
public record ConstraintInstanceHardReward(
    List<ResourceTimefoldInstance> parameters,
    Function<List<ResourceTimefoldInstance>, Boolean> evaluationFunction
) implements ConstraintInstanceTimefoldInstance {
  public boolean evaluate() {
    return evaluationFunction.apply(parameters);
  }
}
