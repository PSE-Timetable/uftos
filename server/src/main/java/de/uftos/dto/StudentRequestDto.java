package de.uftos.dto;

import de.uftos.entities.Student;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A data transfer object used in the student HTTP requests.
 *
 * @param firstName the first name of the student.
 * @param lastName  the last name of the student.
 * @param tagIds    the tags associated with the student.
 */
public record StudentRequestDto(@NotEmpty String firstName, @NotEmpty String lastName, @NotNull List<String> groupIds,
                                @NotNull List<String> tagIds) {

  /**
   * Maps the information from the data transfer object to a new student entity.
   *
   * @return the new student entity.
   */
  public Student map() {
    return new Student(this.firstName(), this.lastName(), this.groupIds(), this.tagIds());
  }
}
