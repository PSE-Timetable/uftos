package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;
import java.util.List;

/**
 * The data transfer object of a teacher for communication with solvers.
 */
public record TeacherProblemDto(
    String id,
    List<String> tagIds,
    List<String> lessonIds,
    List<String> subjectIds
) implements ResourceProblemDto {
  @Override
  public String getId() {
    return id;
  }

  @Override
  public ResourceType getType() {
    return ResourceType.TEACHER;
  }
}
