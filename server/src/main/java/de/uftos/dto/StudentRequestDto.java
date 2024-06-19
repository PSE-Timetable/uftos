package de.uftos.dto;

import de.uftos.entities.Student;
import java.util.List;

/**
 * A data transfer object used in the student HTTP requests.
 *
 * @param firstName the first name of the student.
 * @param lastName  the last name of the student.
 * @param tagIds    the tags associated with the student.
 */
public record StudentRequestDto(String firstName, String lastName, List<String> tagIds) {

  /**
   * Maps the information from the data transfer object to a new student entity.
   *
   * @return the new student entity.
   */
  public Student map() {
    return new Student(this.firstName(), this.lastName(), this.tagIds());
  }
}
