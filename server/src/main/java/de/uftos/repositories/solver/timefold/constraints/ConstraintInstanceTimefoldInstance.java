package de.uftos.repositories.solver.timefold.constraints;

import de.uftos.dto.solver.RewardPenalize;
import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * This interface models a constraint instance, which can be used by the Timefold solver.
 */
public record ConstraintInstanceTimefoldInstance(
    List<ResourceTimefoldInstance> arguments,
    Function<List<ResourceTimefoldInstance>, Boolean> evaluationFunction,
    RewardPenalize rewardPenalize
) {
  /**
   * Evaluates the evaluation function with using the parameters of the constraint instance.
   *
   * @return the value to which the evaluation function evaluates to.
   */
  public boolean evaluate(HashMap<String, ResourceTimefoldInstance> resources) {
    List<ResourceTimefoldInstance> args = new ArrayList<>();
    for (ResourceTimefoldInstance resource : arguments) {
      args.add(resources.get(resource.getId()));
    }
    return evaluationFunction.apply(args);
  }
}
