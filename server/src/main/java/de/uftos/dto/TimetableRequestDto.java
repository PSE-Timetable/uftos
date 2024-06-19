package de.uftos.dto;

import de.uftos.entities.Timetable;

public record TimetableRequestDto(String name) {
  public Timetable map() {
    return new Timetable(this.name);
  }
}
