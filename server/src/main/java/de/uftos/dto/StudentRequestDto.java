package de.uftos.dto;

import de.uftos.entities.Student;
import java.util.List;

public record StudentRequestDto(String firstName, String lastName, List<String> tagIds) {
  public Student map() {
    return new Student(this.firstName(), this.lastName(), this.tagIds());
  }
}
