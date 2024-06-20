package de.uftos.dto;

import de.uftos.entities.Teacher;
import java.util.List;

/**
 * A data transfer object used in the teacher HTTP requests.
 *
 * @param firstName  the first name of the teacher.
 * @param lastName   the last name of the teacher.
 * @param acronym    the acronym used for the teacher.
 * @param subjectIds the id of the subjects the teacher teaches.
 * @param tagIds     the tags associated with the teacher.
 */
public record TeacherRequestDto(String firstName, String lastName, String acronym,
                                List<String> subjectIds, List<String> tagIds) {

  /**
   * Maps the information from the data transfer object to a new teacher entity.
   *
   * @return the new subject entity.
   */
  public Teacher map() {
    return new Teacher(this.firstName, this.lastName, this.acronym, this.subjectIds, this.tagIds);
  }
}
