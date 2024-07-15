package de.uftos.dto;

import de.uftos.entities.Curriculum;
import de.uftos.entities.LessonsCount;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;


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
