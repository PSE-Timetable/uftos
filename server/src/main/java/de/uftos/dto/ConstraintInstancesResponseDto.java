package de.uftos.dto;

import de.uftos.dto.solver.RewardPenalize;
import de.uftos.entities.ConstraintArgument;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintParameter;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param constraintInstances the instances of the constraints
 * @param displayNames        the display names of the arguments of the constraint instances
 */
public record ConstraintInstancesResponseDto(@NotNull List<SlimInstance> constraintInstances,
                                             @NotNull
                                             List<ConstraintArgumentDisplayName> displayNames,
                                             @NotNull List<ConstraintParameter> parameters) {

  /**
   * Creates a new ConstraintInstancesResponseDto instance with the standard entities
   * and converts it internally to the slim entities.
   *
   * @param constraintInstances the instances of the constraint
   * @param displayNames        the display names for the arguments
   */
  public ConstraintInstancesResponseDto(List<ConstraintInstance> constraintInstances,
                                        List<ConstraintArgumentDisplayName> displayNames) {
    this(constraintInstances.stream().map(SlimInstance::new).toList(),
        displayNames,
        constraintInstances.getFirst().getSignature().getParameters());
  }

  /**
   * An object that contains only the useful data of a constraint instance for the response DTO.
   *
   * @param id        the id of the instance
   * @param type      the reward type
   * @param arguments the arguments (also slim) of the instance
   */
  public record SlimInstance(@NotEmpty String id, @NotNull RewardPenalize type,
                             @NotNull List<SlimArgument> arguments) {
    private SlimInstance(ConstraintInstance constraintInstance) {
      this(constraintInstance.getId(),
          constraintInstance.getType(),
          constraintInstance.getArguments().stream().map(SlimArgument::new).toList());
    }

    private record SlimArgument(@NotEmpty String id, @NotNull String value) {
      private SlimArgument(ConstraintArgument constraintArgument) {
        this(constraintArgument.getId(),
            constraintArgument.getValue());
      }
    }
  }
}
