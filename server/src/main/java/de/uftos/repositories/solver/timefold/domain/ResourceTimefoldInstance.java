package de.uftos.repositories.solver.timefold.domain;

import de.uftos.dto.ResourceType;

/**
 * This interface models a resource that can be used by the Timefold solver.
 */
public interface ResourceTimefoldInstance {
  /**
   * Gets the type of the resource.
   *
   * @return the type of the resource.
   */
  ResourceType getResourceType();

  /**
   * Gets the type id the resource.
   *
   * @return the id of the resource.
   */
  String getId();
}
