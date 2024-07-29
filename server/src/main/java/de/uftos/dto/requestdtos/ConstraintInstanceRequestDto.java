package de.uftos.dto.requestdtos;

import de.uftos.dto.solver.RewardPenalize;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A data transfer object used in the constraint HTTP requests.
 *
 * @param arguments a mapping of parameter name to value.
 * @param type      the type of the specific constraint instance.
 */
public record ConstraintInstanceRequestDto(@NotNull List<ConstraintArgumentRequestDto> arguments,
                                           RewardPenalize type) {
}
