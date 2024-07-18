package de.uftos.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ConstraintArgumentDisplayName(@NotEmpty String id, @NotNull String displayName) {
}
