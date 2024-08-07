package de.uftos.entities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The timetable break object.
 * Represents a break in the displayed timetable.
 */
@Data
@AllArgsConstructor
public class Break {
  @NotNull
  private boolean isLong;

  @PositiveOrZero
  @NotNull
  private int afterSlot;

  @Positive
  @NotNull
  private int length;
}
