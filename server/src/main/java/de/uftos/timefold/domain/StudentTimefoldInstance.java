package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.entities.ResourceType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIdentityInfo(scope = StudentGroupTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class StudentTimefoldInstance implements ResourceTimefoldInstance {

    public StudentTimefoldInstance(int id) {
        this.id = id;
    }

    @PlanningId
    private int id;
    private List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
    private List<StudentGroupTimefoldInstance> studentGroupList = new ArrayList<>();

    @Override
    public ResourceType getResourceType() {
        return ResourceType.STUDENT;
    }
}
