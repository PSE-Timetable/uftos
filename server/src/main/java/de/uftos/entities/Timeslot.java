package de.uftos.entities;

import java.util.List;

public record Timeslot(
        int id,
        int dayOfWeek,
        int slot,
        List<Integer> providedTagsIdList,
        List<Integer> lessonIdList
) implements Resource {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TIMESLOT;
    }
}
