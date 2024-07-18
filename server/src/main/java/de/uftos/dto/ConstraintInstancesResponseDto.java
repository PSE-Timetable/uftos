package de.uftos.dto;

import de.uftos.entities.ConstraintInstance;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param constraintInstances the instances of the constraints
 * @param displayNames        the display names of the arguments of the constraint instances
 */
public record ConstraintInstancesResponseDto(@NotNull List<ConstraintInstance> constraintInstances,
                                             @NotNull
                                             List<ConstraintArgumentDisplayName> displayNames) {
}
