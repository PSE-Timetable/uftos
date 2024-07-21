package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;
import java.util.List;

/**
 * The data transfer object of a grade for communication with solvers.
 */
public record GradeProblemDto(
    String id,
    List<String> tagIds,
    List<String> studentGroupIds
) implements ResourceProblemDto {
  @Override
  public String getId() {
    return id;
  }

  @Override
  public ResourceType getType() {
    return ResourceType.GRADE;
  }
}
