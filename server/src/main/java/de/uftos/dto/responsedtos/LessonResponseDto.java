package de.uftos.dto.responsedtos;

import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.Timetable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public record LessonResponseDto(@NotNull List<BulkLesson> lessons,
                                @NotNull List<Teacher> teachers,
                                @NotNull List<StudentGroupResponseDto> groups,
                                @NotNull List<GradeResponseDto> grades, @NotNull List<Room> rooms,
                                @NotNull List<Subject> subjects, @NotNull Timetable timetable) {
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
    Set<StudentGroupResponseDto> groups = new HashSet<>();
    Timetable timetable = lessons.isEmpty() ? null : lessons.getFirst().getTimetable();

    for (Lesson lesson : lessons) {
      List<String> gradeIds = new ArrayList<>();
      for (Grade grade : lesson.getStudentGroup().getGrades()) {
        gradeIds.add(grade.getId());
        grades.add(grade);
      }
      bulkLessons.add(new BulkLesson(lesson, gradeIds));
      teachers.add(lesson.getTeacher());
      rooms.add(lesson.getRoom());
      subjects.add(lesson.getSubject());
      groups.add(new StudentGroupResponseDto(lesson.getStudentGroup()));
    }

    grades.stream().map(GradeResponseDto::createResponseDtoFromGrade)
        .forEach(gradeResponseDtos::add);

    return new LessonResponseDto(bulkLessons, teachers.stream().toList(), groups.stream().toList(),
        gradeResponseDtos,
        rooms.stream().toList(), subjects.stream().toList(), timetable);
  }

  private record BulkLesson(@NotEmpty String id, @PositiveOrZero int index,
                            @NotEmpty String teacherId,
                            @NotEmpty String roomId,
                            @NotEmpty String groupId,
                            @NotNull List<String> gradeIds, @NotNull Timeslot timeslot,
                            @NotEmpty String subjectId, @NotNull Timetable timetable) {
    private BulkLesson(Lesson lesson, List<String> gradesId) {
      this(lesson.getId(), lesson.getIndex(), lesson.getTeacher().getId(),
          lesson.getRoom().getId(), lesson.getStudentGroup().getId(), gradesId,
          lesson.getTimeslot(),
          lesson.getSubject().getId(), lesson.getTimetable());
    }
  }
}

