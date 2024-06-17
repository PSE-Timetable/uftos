package de.uftos.entities;

import java.util.List;
public record Teacher(
        int id,
        List<Integer> providedTagsIdList,
        List<Integer> subjectIdList,
        List<Integer> lessonIdList
) implements Resource {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEACHER;
    }
}
