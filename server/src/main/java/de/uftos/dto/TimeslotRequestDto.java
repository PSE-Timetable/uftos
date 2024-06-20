package de.uftos.dto;

import de.uftos.entities.Timeslot;
import java.util.List;

/**
 * A data transfer object used in the timeslot HTTP requests.
 *
 * @param day    the day of the timeslot.
 * @param slot   the slot in the day of the timeslot.
 * @param tagIds the IDs of the tags associated with the timeslot.
 */
public record TimeslotRequestDto(Weekday day, int slot, List<String> tagIds) {

  /**
   * Maps the information from the data transfer object to a new timeslot entity.
   *
   * @return the new timeslot entity.
   */
  public Timeslot map() {
    return new Timeslot(this.day, this.slot, this.tagIds);
  }
}
