package de.uftos.dto.requestdtos;

import de.uftos.entities.LessonsCount;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param subjectId the id of the subject.
 * @param count     the number of timeslots this subject will be hold per week.
 */
public record LessonsCountRequestDto(@NotEmpty String subjectId,
                                     @PositiveOrZero @NotNull int count) {
  /**
   * Maps the information from the data transfer object to a new lesson count entity.
   *
   * @return the new lesson count entity.
   */
  public LessonsCount map() {
    return new LessonsCount(subjectId, count);
  }
}
