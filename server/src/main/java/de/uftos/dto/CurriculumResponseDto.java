package de.uftos.dto;

import de.uftos.entities.LessonsCount;
import java.util.List;

public record CurriculumResponseDto(String id, String name, GradeResponseDto grade,
                                    List<LessonsCount> lessonsCounts) {
}
