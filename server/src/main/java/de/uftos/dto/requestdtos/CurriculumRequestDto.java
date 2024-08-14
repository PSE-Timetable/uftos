package de.uftos.dto.requestdtos;

import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param gradeId       the grade id the curriculum belongs to.
 * @param name          the name of the curriculum.
 * @param lessonsCounts a list of objects representing how often a lesson should be scheduled.
 */
public record CurriculumRequestDto(@NotEmpty String gradeId, @NotEmpty String name,
                                   @NotNull List<LessonsCountRequestDto> lessonsCounts) {


  /**
   * Maps the information from the data transfer object to a new curriculum entity.
   *
   * @param grade the Grade object to be mapped
   * @return the new curriculum entity.
   */
  public Curriculum map(Grade grade) {
    return new Curriculum(grade, this.name, this.lessonsCounts);
  }

}
