package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;
import java.util.List;

/**
 * The data transfer object of a timeslot for communication with solvers.
 */
public record TimeslotProblemDto(
    String id,
    int day,
    int slot,
    List<String> tagIds,
    List<String> lessonIds
) implements ResourceProblemDto {

  @Override
  public ResourceType getType() {
    return ResourceType.TIMESLOT;
  }
}
