package de.uftos.dto;

import de.uftos.entities.Tag;
import java.util.List;

/**
 * A data transfer object used in the grade HTTP responses.
 *
 * @param id              the ID of the grade.
 * @param name            the name of the grade.
 * @param studentGroupIds the IDs of the student groups that are a part of the grade.
 * @param studentIds      the IDs of the students that are a part of the grade.
 * @param tags            the tags associated with the grade.
 */
public record GradeResponseDto(String id, String name, List<String> studentGroupIds,
                               List<String> studentIds, List<Tag> tags) {
}
