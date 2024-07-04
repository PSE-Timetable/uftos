package de.uftos.dto;

import de.uftos.entities.Lesson;

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
public record LessonRequestDto(int index, String teacherId, String studentGroupId, String roomId,
                               String timeslotId, String subjectId, String timetableId) {
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
