package de.uftos.dto;

import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.Timetable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
  /**
   * Creates a LessonResponseDto from a list of lessons. Each lesson has to be in the same
   * timetable.
   *
   * @param lessons the list of lessons.
   * @return the LessonResponseDto containing information about all the lessons.
   */
  public static LessonResponseDto createResponseDtoFromLessons(List<Lesson> lessons) {
    List<BulkLesson> bulkLessons = new ArrayList<>();
    Set<Teacher> teachers = new HashSet<>();
    Set<Grade> grades = new HashSet<>();
    List<GradeResponseDto> gradeResponseDtos = new ArrayList<>();
    Set<Room> rooms = new HashSet<>();
    Set<Subject> subjects = new HashSet<>();
    // Sets to avoid duplicates
    Timetable timetable = lessons.isEmpty() ? null : lessons.getFirst().getTimetable();

    for (Lesson lesson : lessons) {
      List<String> gradeIds = new ArrayList<>();
      for (Grade grade : lesson.getStudentGroup().getGrades()) {
        gradeIds.add(grade.getId());
        grades.add(grade);
      }
      bulkLessons.add(new BulkLesson(
          lesson.getId(), lesson.getIndex(), lesson.getTeacher().getId(),
          lesson.getRoom().getId(), gradeIds, lesson.getTimeslot(), lesson.getSubject().getId()));
      teachers.add(lesson.getTeacher());
      rooms.add(lesson.getRoom());
      subjects.add(lesson.getSubject());
    }
    grades.stream().map(GradeResponseDto::createResponseDtoFromGrade)
        .forEach(gradeResponseDtos::add);

    return new LessonResponseDto(bulkLessons, teachers.stream().toList(), gradeResponseDtos,
        rooms.stream().toList(), subjects.stream().toList(), timetable);
  }

  private record BulkLesson(String id, int index, String teacherId, String roomId,
                            List<String> gradeIds, Timeslot timeslot,
                            String subjectId) {
  }
}

