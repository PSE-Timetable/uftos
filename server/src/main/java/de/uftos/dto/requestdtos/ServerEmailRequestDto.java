package de.uftos.dto.requestdtos;

import jakarta.validation.constraints.Email;

/**
 * A data transfer object used in the server HTTP requests.
 *
 * @param email the email address for notifications.
 */
public record ServerEmailRequestDto(@Email String email) {
}
