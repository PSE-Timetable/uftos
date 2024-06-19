package de.uftos.dto;

import de.uftos.entities.StudentGroup;
import java.util.List;

public record StudentGroupRequestDto(String name, List<String> studentIds, List<String> gradeIds,
                                     List<String> tagIds) {
  public StudentGroup map() {
    return new StudentGroup(this.name, this.studentIds, this.gradeIds, this.tagIds);
  }
}
