package de.uftos.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimetableMetadata {
  @Positive
  @NotNull
  private int timeslotLength;
  @NotBlank
  @NotNull
  private String startTime;
  @NotNull
  private Break[] breaks;
}
