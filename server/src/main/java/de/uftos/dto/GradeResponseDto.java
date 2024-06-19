package de.uftos.dto;

import de.uftos.entities.Tag;
import java.util.List;

public record GradeResponseDto(String id, String name, List<String> studentGroupIds,
                               List<String> studentIds, List<Tag> tags) {
}
