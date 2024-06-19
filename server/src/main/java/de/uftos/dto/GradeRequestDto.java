package de.uftos.dto;

import de.uftos.entities.Grade;
import java.util.List;

public record GradeRequestDto(String name, List<String> studentGroupsIds, List<String> tagIds) {
  public Grade map() {
    return new Grade(this.name, this.studentGroupsIds, this.tagIds);
  }
}
