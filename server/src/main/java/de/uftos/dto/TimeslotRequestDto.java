package de.uftos.dto;

import de.uftos.entities.Timeslot;
import java.util.List;

public record TimeslotRequestDto(Weekday day, int slot, List<String> tagIds) {
  public Timeslot map() {
    return new Timeslot(this.day, this.slot, this.tagIds);
  }
}
