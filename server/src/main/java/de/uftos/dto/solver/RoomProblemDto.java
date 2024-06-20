package de.uftos.dto.solver;

import java.util.List;

/**
 * The data transfer object of a Room for communication with solvers.
 */
public record RoomProblemDto(
    String id,
    List<String> tagIds,
    List<String> lessonIds
) {
}
