package de.uftos.dto.responsedtos;

import de.uftos.entities.Lesson;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import java.util.List;

public record StudentGroupResponseDto(String id,
                                      String name,
                                      List<Student> students,
                                      List<GradeResponseDto> grades,
                                      List<Tag> tags,
                                      List<Lesson> lessons,
                                      List<Subject> subjects) {
  public StudentGroupResponseDto(StudentGroup studentGroup) {
    this(studentGroup.getId(),
        studentGroup.getName(),
        studentGroup.getStudents(),
        studentGroup.getGrades().stream().map(GradeResponseDto::createResponseDtoFromGrade).toList(),
        studentGroup.getTags(),
        studentGroup.getLessons(),
        studentGroup.getSubjects());
  }
}
