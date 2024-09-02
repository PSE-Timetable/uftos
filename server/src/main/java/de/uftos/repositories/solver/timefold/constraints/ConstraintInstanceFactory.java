package de.uftos.repositories.solver.timefold.constraints;

import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.ResourceProblemDto;
import de.uftos.dto.solver.RewardPenalize;
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

    List<ResourceTimefoldInstance> arguments = new ArrayList<>();

    arguments.add(resources.get("this"));

    for (ResourceProblemDto param : instance.parameters()) {
      ResourceTimefoldInstance resource = resources.get(param.id());
      if (resource.getResourceType() != param.getType()) {
        throw new IllegalArgumentException();
      }
      arguments.add(resource);
    }

    RewardPenalize rewardPenalize = instance.type();
    if (rewardPenalize == null) {
      rewardPenalize = definition.defaultType();
    }

    return new ConstraintInstanceTimefoldInstance(arguments, definition.evaluationFunction(),
        rewardPenalize);
  }
}
