package de.uftos.dto;

import de.uftos.entities.Curriculum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param gradeId       the grade id the curriculum belongs to.
 * @param lessonsCounts a list of objects representing how often a lesson should be scheduled.
 */
public record CurriculumRequestDto(@NotEmpty String gradeId,
                                   @NotNull List<LessonsCountRequestDto> lessonsCounts) {
  /**
   * Maps the information from the data transfer object to a new curriculum entity.
   *
   * @return the new curriculum entity.
   */
  public Curriculum map() {
    return new Curriculum(this.gradeId, this.lessonsCounts);
  }

}
