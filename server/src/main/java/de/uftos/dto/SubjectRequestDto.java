package de.uftos.dto;

import de.uftos.entities.Subject;
import java.util.List;

/**
 * A data transfer object used in the subject HTTP request.
 *
 * @param name   the name of the subject.
 * @param tagIds the tags associated with the subject.
 */
public record SubjectRequestDto(String name, List<String> tagIds) {

  /**
   * Maps the information from the data transfer object to a new subject entity.
   *
   * @return the new subject entity.
   */
  public Subject map() {
    return new Subject(this.name, this.tagIds);
  }
}
