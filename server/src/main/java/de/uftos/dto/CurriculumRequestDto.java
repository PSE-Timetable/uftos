package de.uftos.dto;

import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.LessonsCount;
import java.util.List;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param grade         the grade the curriculum belongs to.
 * @param lessonsCounts a list of objects representing how often a lesson should be scheduled.
 */
public record CurriculumRequestDto(Grade grade,
                                   List<LessonsCount> lessonsCounts) {
  public Curriculum map() {
    return new Curriculum(this.grade, this.lessonsCounts);
  }

}
