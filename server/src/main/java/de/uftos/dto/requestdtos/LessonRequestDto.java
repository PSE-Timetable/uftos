package de.uftos.dto.requestdtos;

import de.uftos.entities.Lesson;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * A data transfer object used in the lesson HTTP requests.
 *
 * @param index          the index of the lesson.
 * @param teacherId      the id of the teacher that will hold the lesson.
 * @param studentGroupId the id of the student group that will attend the lesson.
 * @param roomId         the id of the room where the lesson will be hold.
 * @param timeslotId     the id of the timeslot of when the lesson will be hold.
 * @param subjectId      the id of the subject to be hold in the lesson.
 * @param timetableId    the id of the timetable.
 */
public record LessonRequestDto(@PositiveOrZero @NotNull int index, @NotEmpty String teacherId,
                               @NotEmpty String studentGroupId,
                               @NotEmpty String roomId,
                               @NotEmpty String timeslotId, @NotEmpty String subjectId,
                               @NotEmpty String timetableId) {
  /**
   * Maps the information from the data transfer object to a new lesson entity.
   *
   * @return the new lesson entity.
   */
  public Lesson map() { //TODO: update API for defining year
    return new Lesson(index, teacherId, studentGroupId, roomId, timeslotId, subjectId, timetableId,
        "2024");
  }
}
