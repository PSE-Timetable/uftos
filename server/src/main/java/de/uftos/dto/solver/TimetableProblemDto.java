package de.uftos.dto.solver;

import java.util.List;

/**
 * The data transfer object of a timetable for communication with solvers.
 */
public record TimetableProblemDto(
    List<GradeProblemDto> grades,
    List<LessonProblemDto> lessons,
    List<RoomProblemDto> rooms,
    List<StudentGroupProblemDto> studentGroups,
    List<StudentProblemDto> students,
    List<SubjectProblemDto> subjects,
    List<TagProblemDto> tags,
    List<TeacherProblemDto> teachers,
    List<TimeslotProblemDto> timeslots
) {
}
