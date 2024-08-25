package de.uftos.dto.responsedtos;

import de.uftos.entities.Lesson;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * A data transfer object used in the student group HTTP requests.
 *
 * @param id       the id of the student group
 * @param name     the name of the student group
 * @param students the students in the student group
 * @param grades   the grades of the student group
 * @param tags     the tags of the student group
 * @param lessons  the lessons of the student group
 * @param subjects the subjects of the student group
 */
public record StudentGroupResponseDto(@NotEmpty String id,
                                      @NotEmpty String name,
                                      @NotNull List<Student> students,
                                      @NotNull List<GradeResponseDto> grades,
                                      @NotNull List<Tag> tags,
                                      @NotNull List<Lesson> lessons,
                                      @NotNull List<Subject> subjects) {

  /**
   * Creates a new StudentGroupResponseDto from a student group.
   *
   * @param studentGroup the student group
   */
  public StudentGroupResponseDto(StudentGroup studentGroup) {
    this(studentGroup.getId(),
        studentGroup.getName(),
        studentGroup.getStudents(),
        studentGroup.getGrades() == null 
            ? Collections.emptyList()
            : studentGroup.getGrades().stream().map(GradeResponseDto::createResponseDtoFromGrade).toList(),
        studentGroup.getTags(),
        studentGroup.getLessons(),
        studentGroup.getSubjects());
  }
}
