package de.uftos.dto.ucdl;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * The response when attempting to parse a UCDL-file.
 *
 * @param success true when parsing was successful, false when parsing was unsuccessful.
 * @param message contains error information if parsing was unsuccessful.
 */
public record ParsingResponse(
    @NotNull
    boolean success,
    @NotEmpty
    String message
) {
}
