package de.uftos.dto;

import de.uftos.entities.Teacher;
import java.util.List;

public record TeacherRequestDto(String firstName, String lastName, String acronym,
                                List<String> subjectIds, List<String> tagIds) {
  public Teacher map() {
    return new Teacher(this.firstName, this.lastName, this.acronym, this.subjectIds, this.tagIds);
  }
}
