package de.uftos.dto;

import de.uftos.entities.LessonsCount;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param subjectId the id of the subject.
 * @param count     the number of timeslots this subject will be hold per week.
 */
public record LessonsCountRequestDto(String subjectId, int count) {
  /**
   * Maps the information from the data transfer object to a new lesson count entity.
   *
   * @return the new lesson count entity.
   */
  public LessonsCount map() {
    return new LessonsCount(subjectId, count);
  }
}
