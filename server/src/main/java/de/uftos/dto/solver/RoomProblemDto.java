package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;
import java.util.List;

/**
 * The data transfer object of a Room for communication with solvers.
 */
public record RoomProblemDto(
    String id,
    List<String> tagIds,
    List<String> lessonIds
) implements ResourceProblemDto {
  @Override
  public String getId() {
    return id;
  }

  @Override
  public ResourceType getType() {
    return ResourceType.ROOM;
  }
}
