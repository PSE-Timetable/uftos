package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;
import java.util.List;

/**
 * The data transfer object of a subject for communication with solvers.
 */
public record SubjectProblemDto(
    String id,
    List<String> tagIds,
    List<String> lessonIds
) implements ResourceProblemDto {
  @Override
  public ResourceType getType() {
    return ResourceType.SUBJECT;
  }
}
