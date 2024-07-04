package de.uftos.dto;

import de.uftos.entities.Grade;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Tag;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A data transfer object used in the grade HTTP responses.
 *
 * @param id              the ID of the grade.
 * @param name            the name of the grade.
 * @param studentGroupIds the IDs of the student groups that are a part of the grade.
 * @param studentIds      the IDs of the students that are a part of the grade.
 * @param tags            the tags associated with the grade.
 */
public record GradeResponseDto(String id, String name, List<String> studentGroupIds,
                               List<String> studentIds, List<Tag> tags) {

  /**
   * Creates a GradeResponseDto from the provided grade.
   *
   * @param grade the grade from which the DTO is to be created.
   * @return the created GradeResponseDto.
   */
  public static GradeResponseDto createResponseDtoFromGrade(Grade grade) {
    Set<String> studentGroupIds = new HashSet<>();
    Set<String> studentIds = new HashSet<>();
    for (StudentGroup studentGroup : grade.getStudentGroups()) {
      studentGroupIds.add(studentGroup.getId());
      studentGroup.getStudents().stream().map(Student::getId).forEach(studentIds::add);
    }
    return new GradeResponseDto(grade.getId(), grade.getName(), studentGroupIds.stream().toList(),
        studentIds.stream().toList(), grade.getTags());
  }
}
