package de.uftos.dto.ucdl;

/**
 * The response when attempting to parse a UCDL-file.
 *
 * @param success true when parsing was successful, false when parsing was unsuccessful.
 * @param message contains error information if parsing was unsuccessful.
 */
public record ParsingResponse(
    boolean success,
    String message
) {
}
