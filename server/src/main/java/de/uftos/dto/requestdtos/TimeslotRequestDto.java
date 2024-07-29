package de.uftos.dto.requestdtos;

import de.uftos.dto.Weekday;
import de.uftos.entities.Timeslot;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * A data transfer object used in the timeslot HTTP requests.
 *
 * @param day    the day of the timeslot.
 * @param slot   the slot in the day of the timeslot.
 * @param tagIds the IDs of the tags associated with the timeslot.
 */
public record TimeslotRequestDto(@NotNull Weekday day, @PositiveOrZero @NotNull int slot,
                                 @NotNull List<String> tagIds) {

  /**
   * Maps the information from the data transfer object to a new timeslot entity.
   *
   * @return the new timeslot entity.
   */
  public Timeslot map() {
    return new Timeslot(this.day, this.slot, this.tagIds);
  }
}
