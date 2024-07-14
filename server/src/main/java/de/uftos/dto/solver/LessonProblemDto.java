package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;

/**
 * The data transfer object of a Lesson for communication with solvers.
 */
public record LessonProblemDto(
    String id,
    int index,
    String teacherId,
    String studentGroupId,
    String timeslotId,
    String subjectId,
    String roomId
) implements ResourceProblemDto {
  @Override
  public ResourceType getType() {
    return ResourceType.LESSON;
  }
}
