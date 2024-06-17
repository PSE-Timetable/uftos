package de.uftos.entities;

import java.util.List;

public record Room(
        int id,
        List<Integer> providedTagsIdList,
        List<Integer> lessonIdList
) implements Resource {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.ROOM;
    }
}
