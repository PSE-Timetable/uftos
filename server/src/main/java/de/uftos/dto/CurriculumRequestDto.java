package de.uftos.dto;

import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.LessonsCount;
import java.util.List;

public record CurriculumRequestDto(Grade grade,
                                   List<LessonsCount> lessonsCounts) {
  public Curriculum map() {
    return new Curriculum(this.grade, this.lessonsCounts);
  }

}
