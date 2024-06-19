package de.uftos.dto;

import de.uftos.entities.Room;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.Timetable;
import java.util.List;


public record LessonResponseDto(List<BulkLesson> lessons,
                                List<Teacher> teachers,
                                List<GradeResponseDto> grades, List<Room> rooms,
                                List<Subject> subjects, Timetable timetable) {

  private record BulkLesson(String id, int index, String teacherId, String roomId,
                            List<String> gradeIds, List<Tag> tags, Timeslot timeslot,
                            String subjectId) {
  }
}

