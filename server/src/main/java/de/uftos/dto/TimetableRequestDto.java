package de.uftos.dto;

import de.uftos.entities.Timetable;

/**
 * A data transfer object used in the timetable HTTP requests.
 *
 * @param name the name of the timetable.
 */
public record TimetableRequestDto(String name) {

  /**
   * Maps the information from the data transfer object to a new timetable entity.
   *
   * @return the new timetable entity.
   */
  public Timetable map() {
    return new Timetable(this.name);
  }
}
