package de.uftos.dto;

import de.uftos.entities.Room;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.Timetable;
import java.util.List;

/**
 * A data transfer object used in the lessons HTTP responses.
 *
 * @param lessons   the lessons that are given as a response.
 * @param teachers  the teachers of the lessons.
 * @param grades    the grades of which the lessons are a part of.
 * @param rooms     the room in which the lessons take part.
 * @param subjects  the subjects of which lessons take place.
 * @param timetable the timetable of which the lessons are a part of.
 */
public record LessonResponseDto(List<BulkLesson> lessons,
                                List<Teacher> teachers,
                                List<GradeResponseDto> grades, List<Room> rooms,
                                List<Subject> subjects, Timetable timetable) {
  private record BulkLesson(String id, int index, String teacherId, String roomId,
                            List<String> gradeIds, List<Tag> tags, Timeslot timeslot,
                            String subjectId) {
  }
}

