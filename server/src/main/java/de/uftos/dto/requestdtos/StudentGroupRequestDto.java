package de.uftos.dto.requestdtos;

import de.uftos.entities.StudentGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A data transfer object used in student group HTTP requests.
 *
 * @param name       the name of the student group.
 * @param studentIds the IDs of the students that are part of the student group.
 * @param gradeIds   the IDs of the grades that are part of the student group.
 * @param tagIds     the IDs of the tags associated with the student group.
 */
public record StudentGroupRequestDto(@NotEmpty String name, @NotNull List<String> studentIds,
                                     @NotNull List<String> gradeIds,
                                     @NotNull List<String> tagIds,
                                     @NotNull List<String> subjectIds) {
  /**
   * Maps the information from the data transfer object to a new student group entity.
   *
   * @return the new student group entity.
   */
  public StudentGroup map() {
    return new StudentGroup(this.name, this.studentIds, this.tagIds,
        this.subjectIds);
  }
}
