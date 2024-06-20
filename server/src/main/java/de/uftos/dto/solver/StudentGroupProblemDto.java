package de.uftos.dto.solver;

import java.util.List;

/**
 * The data transfer object of a student group for communication with solvers.
 */
public record StudentGroupProblemDto(
    String id,
    String gradeId,
    List<String> tagIds,
    List<String> lessonIds,
    List<String> studentIds
) {
}
