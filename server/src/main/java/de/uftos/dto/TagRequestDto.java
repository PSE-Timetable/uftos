package de.uftos.dto;

import de.uftos.entities.Tag;

public record TagRequestDto(String tagName) {
  public Tag map() {
    return new Tag(null, this.tagName);
  }
}
