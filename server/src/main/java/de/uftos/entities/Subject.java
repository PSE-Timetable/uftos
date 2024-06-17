package de.uftos.entities;

import java.util.List;

public record Subject(
        int id,
        List<Integer> providedTagsIdList,
        List<Integer> lessonIdList,
        List<Integer> teacherIdList
) implements Resource {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SUBJECT;
    }
}
