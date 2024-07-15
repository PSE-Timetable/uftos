package de.uftos.timefold.domain;

import de.uftos.dto.ResourceType;
import lombok.Getter;

public class Number implements ResourceTimefoldInstance {
  @Getter
  private final int value;

  public Number(int value) {
    this.value = value;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.NUMBER;
  }
}
