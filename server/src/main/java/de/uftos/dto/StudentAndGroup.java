package de.uftos.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * A data transfer object used to define a relation between a student and a student group.
 *
 * @param studentId      the ID of the student.
 * @param studentGroupId the ID of the student group.
 */
public record StudentAndGroup(@NotEmpty String studentId, @NotEmpty String studentGroupId) {
}
