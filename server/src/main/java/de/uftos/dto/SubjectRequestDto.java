package de.uftos.dto;

import de.uftos.entities.Subject;
import java.util.List;

public record SubjectRequestDto(String name, List<String> tagIds) {
  public Subject map() {
    return new Subject(this.name, this.tagIds);
  }
}
