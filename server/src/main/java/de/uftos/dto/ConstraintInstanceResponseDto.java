package de.uftos.dto;

import de.uftos.controller.ConstraintArgumentDisplayName;
import de.uftos.entities.ConstraintInstance;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ConstraintInstanceResponseDto(@NotNull List<ConstraintInstance> constraintInstances,
                                            @NotNull List<ConstraintArgumentDisplayName> displayNames) {
}
