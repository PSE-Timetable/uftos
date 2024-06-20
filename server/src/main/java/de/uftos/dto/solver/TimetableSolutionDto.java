package de.uftos.dto.solver;

import java.util.List;

/**
 * A solved timetable instance.
 *
 * @param lessons all lessons of the timetable.
 */
public record TimetableSolutionDto(
    List<LessonProblemDto> lessons
) {
}
