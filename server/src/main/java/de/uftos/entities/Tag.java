package de.uftos.entities;

import java.util.List;

public record Tag(
        int id,
        List<Integer> studentIdList,
        List<Integer> studentGroupIdList,
        List<Integer> teacherIdList,
        List<Integer> roomIdList,
        List<Integer> subjectIdList,
        List<Integer> gradeIdList,
        List<Integer> timeslotIdList
) implements Resource {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TAG;
    }
}
