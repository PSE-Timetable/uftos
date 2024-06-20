package de.uftos.dto.solver;

import java.util.List;

/**
 * The data transfer object of a teacher for communication with solvers.
 */
public record TeacherProblemDto(
    String id,
    List<String> tagIds,
    List<String> lessonIds,
    List<String> subjectIds
) {
}
