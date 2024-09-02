package de.uftos.dto.requestdtos;

import de.uftos.entities.Timetable;
import jakarta.validation.constraints.NotEmpty;

/**
 * A data transfer object used in the timetable HTTP requests.
 *
 * @param name the name of the timetable.
 */
public record TimetableRequestDto(@NotEmpty String name) {

  /**
   * Maps the information from the data transfer object to a new timetable entity.
   *
   * @return the new timetable entity.
   */
  public Timetable map() {
    return new Timetable(this.name);
  }
}
