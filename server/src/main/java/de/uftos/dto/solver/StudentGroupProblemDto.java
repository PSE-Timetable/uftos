package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;
import java.util.List;

/**
 * The data transfer object of a student group for communication with solvers.
 */
public record StudentGroupProblemDto(
    String id,
    String gradeId,
    List<String> tagIds,
    List<String> lessonIds,
    List<String> studentIds
) implements ResourceProblemDto {
  @Override
  public String getId() {
    return id;
  }

  @Override
  public ResourceType getType() {
    return ResourceType.STUDENT_GROUP;
  }
}
