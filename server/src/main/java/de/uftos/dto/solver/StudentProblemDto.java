package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;
import java.util.List;

/**
 * The data transfer object of a student for communication with solvers.
 */
public record StudentProblemDto(
    String id,
    List<String> tagIds,
    List<String> studentGroupIds
) implements ResourceProblemDto {

  @Override
  public ResourceType getType() {
    return ResourceType.STUDENT;
  }
}
