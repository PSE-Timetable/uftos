package de.uftos.dto;

/**
 * A data transfer object used in the server HTTP requests.
 *
 * @param studentCount  the count of all students.
 * @param teacherCount  the count of all teachers.
 * @param classCount    the count of all classes.
 * @param roomCount     the count of all rooms.
 * @param resourceCount the count of all resources.
 */
public record ServerStatisticsResponseDto(long studentCount, long teacherCount, long classCount,
                                          long roomCount, long resourceCount) {
}
