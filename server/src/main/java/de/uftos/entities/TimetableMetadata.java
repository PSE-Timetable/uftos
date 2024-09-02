package de.uftos.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

  @Positive
  @NotNull
  private int timeslotsAmount;

  @NotBlank
  private String startTime;

  @NotNull
  private Break[] breaks;
}
