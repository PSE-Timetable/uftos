package de.uftos.dto;

import de.uftos.entities.Grade;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A data transfer object used in the grade HTTP requests.
 *
 * @param name             the name of the grade.
 * @param studentGroupsIds the IDs of the student group that are a part of the grade.
 * @param tagIds           the IDs of the tags associated with the grade.
 */
public record GradeRequestDto(@NotEmpty String name, @NotNull List<String> studentGroupsIds,
                              @NotNull List<String> tagIds) {
  public Grade map() {
    return new Grade(this.name, this.studentGroupsIds, this.tagIds);
  }
}
