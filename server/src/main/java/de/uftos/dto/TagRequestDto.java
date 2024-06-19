package de.uftos.dto;

import de.uftos.entities.Tag;

/**
 * A data transfer object used in the tag HTTP requests.
 *
 * @param tagName the name of the tag.
 */
public record TagRequestDto(String tagName) {

  /**
   * Maps the information from the data transfer object to a new tag entity.
   *
   * @return the new tag entity.
   */
  public Tag map() {
    return new Tag(null, this.tagName);
  }
}
