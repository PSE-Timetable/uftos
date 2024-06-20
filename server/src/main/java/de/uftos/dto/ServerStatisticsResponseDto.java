package de.uftos.dto;

public record ServerStatisticsResponseDto(long studentCount, long teacherCount, long classCount,
                                          long roomCount, long resourceCount) {
}
