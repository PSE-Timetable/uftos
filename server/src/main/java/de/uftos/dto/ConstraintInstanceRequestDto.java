package de.uftos.dto;

import de.uftos.dto.solver.RewardPenalize;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * A data transfer object used in the constraint HTTP requests.
 *
 * @param arguments a mapping of parameter name to value.
 * @param type      the type of the specific constraint instance.
 */
public record ConstraintInstanceRequestDto(@NotNull Map<String, String> arguments,
                                           RewardPenalize type) {
}
