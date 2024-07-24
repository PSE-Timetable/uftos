package de.uftos.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The timetable metadata object.
 * Contains various metadata of the timetable relevant for displaying it.
 */
@Data
@AllArgsConstructor
public class TimetableMetadata {
  @Positive
  @NotNull
  private int timeslotLength;

  @NotBlank
  private String startTime;

  @NotNull
  private Break[] breaks;
}
