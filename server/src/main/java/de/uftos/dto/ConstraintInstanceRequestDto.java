package de.uftos.dto;

import de.uftos.dto.solver.RewardPenalize;
import java.util.Map;

public record ConstraintInstanceRequestDto(Map<String, String> arguments, RewardPenalize type) {
}
