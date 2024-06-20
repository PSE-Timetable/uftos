package de.uftos.dto.solver;

import java.util.List;

/**
 * The data transfer object of a tag for communication with solvers.
 */
public record TagProblemDto(
    String id,
    List<String> gradeIds,
    List<String> roomIds,
    List<String> studentIds,
    List<String> studentGroupIds,
    List<String> subjectIds,
    List<String> teacherIds,
    List<String> timeslotIds
) {
}
