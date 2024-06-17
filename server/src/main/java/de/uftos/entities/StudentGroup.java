package de.uftos.entities;

import java.util.List;

public record StudentGroup(
        int id,
        int gradeId,
        List<Integer> providedTagsIdList,
        List<Integer> studentIdList,
        List<Integer> lessonIdList
) implements Resource {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.STUDENT_GROUP;
    }
}
