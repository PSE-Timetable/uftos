package de.uftos.repositories.solver.timefold.constraints.constraintinstances;

import de.uftos.dto.solver.RewardPenalize;
import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * This class models a constraint instance, which can be used by the Timefold solver.
 * This instance is soft penalized when the evaluation function returns true.
 *
 * @param parameters         the resources used in this constraint instance.
 * @param evaluationFunction the function used to evaluate this constraint instance.
 */
public record ConstraintInstanceSoftPenalize(
    List<ResourceTimefoldInstance> parameters,
    Function<List<ResourceTimefoldInstance>, Boolean> evaluationFunction
) implements ConstraintInstanceTimefoldInstance {
  @Override
  public boolean evaluate(HashMap<String, ResourceTimefoldInstance> resources) {
    List<ResourceTimefoldInstance> params = new ArrayList<>();
    for (ResourceTimefoldInstance resource : parameters) {
      params.add(resources.get(resource.getId()));
    }
    try {
      return evaluationFunction.apply(params);
    } catch (NullPointerException e) {
      return true;
    }
  }

  @Override
  public RewardPenalize rewardPenalize() {
    return RewardPenalize.SOFT_PENALIZE;
  }
}
