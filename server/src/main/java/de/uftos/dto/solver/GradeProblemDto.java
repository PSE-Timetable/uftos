package de.uftos.dto.solver;

import java.util.List;

/**
 * The data transfer object of a grade for communication with solvers.
 */
public record GradeProblemDto(
    String id,
    List<String> tagIds,
    List<String> studentGroupIds
) {
}
