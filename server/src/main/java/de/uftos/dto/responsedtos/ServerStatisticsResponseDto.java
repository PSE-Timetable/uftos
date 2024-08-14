package de.uftos.dto.responsedtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * A data transfer object used in the server HTTP requests.
 *
 * @param studentCount  the count of all students.
 * @param teacherCount  the count of all teachers.
 * @param classCount    the count of all classes.
 * @param roomCount     the count of all rooms.
 * @param resourceCount the count of all resources.
 */
public record ServerStatisticsResponseDto(@PositiveOrZero @NotNull long studentCount,
                                          @PositiveOrZero @NotNull long teacherCount,
                                          @PositiveOrZero @NotNull long classCount,
                                          @PositiveOrZero @NotNull long roomCount,
                                          @PositiveOrZero @NotNull long resourceCount) {
}
