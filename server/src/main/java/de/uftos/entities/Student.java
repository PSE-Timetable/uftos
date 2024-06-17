package de.uftos.entities;

import java.util.List;

public record Student (
        int id,
        List<Integer> providedTagsIdList,
        List<Integer> studentGroupIdList
) implements Resource {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.STUDENT;
    }
}
