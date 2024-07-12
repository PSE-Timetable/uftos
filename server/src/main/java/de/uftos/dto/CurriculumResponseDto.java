package de.uftos.dto;

import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.LessonsCount;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param id            the id of the curriculum.
 * @param name          the name of the curriculum.
 * @param grade         the respective grade the curriculum belongs to.
 * @param lessonsCounts a list of objects representing how often a lesson should be scheduled.
 */
public record CurriculumResponseDto(@NotEmpty String id, @NotEmpty String name,
                                    @NotNull GradeResponseDto grade,
                                    @NotNull List<LessonsCount> lessonsCounts) {


  /**
   * Creates a CurriculumResponseDto from a Curriculum.
   *
   * @param curriculum the Curriculum whose information should be included
   *                   in the CurriculumResponseDto.
   * @return the CurriculumResponseDto containing information about the Curriculum.
   */
  public static CurriculumResponseDto createResponseDtoFromCurriculum(Curriculum curriculum) {
    GradeResponseDto gradeResponseDto =
        GradeResponseDto.createResponseDtoFromGrade(curriculum.getGrade());
    return new CurriculumResponseDto(curriculum.getId(), curriculum.getName(), gradeResponseDto,
        curriculum.getLessonsCounts());
  }
}
