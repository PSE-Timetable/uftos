package de.uftos.entities;

import java.util.List;

public record Grade(
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
        return ResourceType.GRADE;
    }
}
