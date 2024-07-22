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
                                             @NotNull List<ConstraintArgumentDisplayName> displayNames,
                                             @NotNull List<ConstraintParameter> parameters) {

  public ConstraintInstancesResponseDto(List<ConstraintInstance> constraintInstances,
                                        List<ConstraintArgumentDisplayName> displayNames) {
    this(constraintInstances.stream().map(SlimInstance::new).toList(),
        displayNames,
        constraintInstances.getFirst().getSignature().getParameters());
  }

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
