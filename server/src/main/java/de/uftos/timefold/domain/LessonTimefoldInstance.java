package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import de.uftos.entities.Lesson;
import de.uftos.entities.ResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @PlanningEntity
public class LessonTimefoldInstance implements ResourceTimefoldInstance{

    public LessonTimefoldInstance() {
    }

    public LessonTimefoldInstance(int id, int index, SubjectTimefoldInstance subject, StudentGroupTimefoldInstance studentGroup) {
        this.id = id;
        this.index = index;
        this.subject = subject;
        this.studentGroup = studentGroup;
    }

    public LessonTimefoldInstance(int id, int index, SubjectTimefoldInstance subject, TeacherTimefoldInstance teacher, StudentGroupTimefoldInstance studentGroup) {
        this.id = id;
        this.index = index;
        this.subject = subject;
        this.teacher = teacher;
        this.studentGroup = studentGroup;
    }

    @PlanningId
    private int id;

    private int index;

    @JsonIdentityReference
    @PlanningVariable
    private TimeslotTimefoldInstance timeslot;

    @JsonIdentityReference
    @PlanningVariable
    private TeacherTimefoldInstance teacher;

    @JsonIdentityReference
    @PlanningVariable
    private StudentGroupTimefoldInstance studentGroup;

    @JsonIdentityReference
    @PlanningVariable
    private SubjectTimefoldInstance subject;

    @JsonIdentityReference
    @PlanningVariable
    private RoomTimefoldInstance room;

    @Override
    public ResourceType getResourceType() {
        return ResourceType.LESSON;
    }
}
