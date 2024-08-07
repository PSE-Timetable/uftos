package de.uftos.repositories.solver.timefold.domain;

import de.uftos.dto.ResourceType;
import lombok.Getter;

/**
 * This class models a number that can be used by the Timefold solver.
 */
public class NumberTimefoldInstance implements ResourceTimefoldInstance {
  @Getter
  private final int value;

  public NumberTimefoldInstance(int value) {
    this.value = value;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.NUMBER;
  }

  @Override
  public String getId() {
    return "number:" + value;
  }
}
