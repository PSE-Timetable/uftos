package de.uftos.timefold.domain;

import de.uftos.dto.ResourceType;

public class Number implements ResourceTimefoldInstance {
  @Override
  public ResourceType getResourceType() {
    return ResourceType.NUMBER;
  }
}
