package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @PlanningEntity
public class LessonTimefoldInstance {
    @PlanningId
    private int id;
    @JsonIdentityReference
    @PlanningVariable
    private int timeslotId;
    @JsonIdentityReference
    @PlanningVariable
    private int teacherId;
    @JsonIdentityReference
    @PlanningVariable
    private int studentGroupId;
    @JsonIdentityReference
    @PlanningVariable
    private int subjectId;
    @JsonIdentityReference
    @PlanningVariable
    private int roomId;
}
