package de.uftos.entities;

import java.util.List;

public record Timetable (
    List<Grade> grades,
    List<Room> rooms,
    List<StudentGroup> studentGroups,
    List<Student> students,
    List<Subject> subjects,
    List<Teacher> teachers,
    List<Timeslot> timeslots,
    List<Lesson> lessons,
    List<Tag> tags
) implements Resource {

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TIMETABLE;
    }
}
