package de.uftos.dto.responsedtos;

import jakarta.validation.constraints.NotEmpty;

/**
 * A data transfer object used in the server HTTP requests.
 *
 * @param email the email address for notifications.
 */
public record ServerEmailResponseDto(@NotEmpty String email) {
}
