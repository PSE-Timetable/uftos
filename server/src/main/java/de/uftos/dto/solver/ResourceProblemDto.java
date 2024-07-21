package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;

/**
 * An instance of a resource.
 */
public interface ResourceProblemDto {
  /**
   * Gets the uuid of the resource.
   *
   * @return the uuid of the resource.
   */
  String getId();

  /**
   * Gets the type of the resource.
   *
   * @return the type of the resource.
   */
  ResourceType getType();
}
