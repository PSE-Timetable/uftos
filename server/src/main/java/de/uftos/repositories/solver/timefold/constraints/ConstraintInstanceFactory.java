package de.uftos.repositories.solver.timefold.constraints;

import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.ResourceProblemDto;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceHardPenalize;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceHardReward;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceSoftPenalize;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceSoftReward;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
  public static ConstraintInstanceTimefoldInstance getConstraintInstance(
      ConstraintInstanceDto instance,
      HashMap<String, ConstraintDefinitionTimefoldInstance> definitions,
      HashMap<String, ResourceTimefoldInstance> resources
  ) {

    ConstraintDefinitionTimefoldInstance definition = definitions.get(instance.definitionName());

    List<ResourceTimefoldInstance> params = new ArrayList<>();

    for (ResourceProblemDto param : instance.parameters()) {
      ResourceTimefoldInstance resource = resources.get(param.getId());
      if (resource.getResourceType() != param.getType()) {
        throw new IllegalArgumentException();
      }
      params.add(resource);
    }

    RewardPenalize rewardPenalize = instance.type();

    if (rewardPenalize == null) {
      rewardPenalize = definition.defaultType();
    }

    return switch (rewardPenalize) {
      case HARD_PENALIZE ->
          new ConstraintInstanceHardPenalize(params, definition.evaluationFunction());
      case SOFT_PENALIZE ->
          new ConstraintInstanceSoftPenalize(params, definition.evaluationFunction());
      case HARD_REWARD -> new ConstraintInstanceHardReward(params, definition.evaluationFunction());
      case SOFT_REWARD -> new ConstraintInstanceSoftReward(params, definition.evaluationFunction());
    };
  }
}
