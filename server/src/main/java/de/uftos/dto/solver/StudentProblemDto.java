package de.uftos.dto.solver;

import java.util.List;

/**
 * The data transfer object of a student for communication with solvers.
 */
public record StudentProblemDto(
    String id,
    List<String> tagIds,
    List<String> studentGroupIds
) {
}
