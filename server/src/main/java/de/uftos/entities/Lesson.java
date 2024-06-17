package de.uftos.entities;

public record  Lesson(
        int id,
        int index,
        int timeslotId,
        int teacherId,
        int studentGroupId,
        int subjectId,
        int roomId
) implements Resource {

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.LESSON;
    }
}
