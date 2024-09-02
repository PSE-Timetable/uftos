package de.uftos.dto.responsedtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * A data transfer object used in the server HTTP requests.
 *
 * @param studentCount    the count of all students.
 * @param teacherCount    the count of all teachers.
 * @param gradeCount      the count of all grades.
 * @param roomCount       the count of all rooms.
 * @param constraintCount the count of all constraint signatures.
 */
public record ServerStatisticsResponseDto(@PositiveOrZero @NotNull long studentCount,
                                          @PositiveOrZero @NotNull long teacherCount,
                                          @PositiveOrZero @NotNull long gradeCount,
                                          @PositiveOrZero @NotNull long roomCount,
                                          @PositiveOrZero @NotNull long constraintCount) {
}
