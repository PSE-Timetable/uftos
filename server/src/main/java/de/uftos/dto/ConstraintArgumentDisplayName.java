package de.uftos.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * A data transfer object used in the curriculum HTTP requests.
 *
 * @param id          the id of the resource
 * @param displayName the name to be displayed for this resource
 */
public record ConstraintArgumentDisplayName(@NotEmpty String id, @NotEmpty String displayName) {
}
