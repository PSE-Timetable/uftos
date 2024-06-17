package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.entities.ResourceType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIdentityInfo(scope =TagTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TagTimefoldInstance implements ResourceTimefoldInstance{

    public TagTimefoldInstance(int id) {
        this.id = id;
    }

    @PlanningId
    private int id;
    private List<StudentTimefoldInstance> studentList = new ArrayList<>();
    private List<StudentGroupTimefoldInstance> studentGroupList = new ArrayList<>();
    private List<TeacherTimefoldInstance> teacherList = new ArrayList<>();
    private List<RoomTimefoldInstance> roomList = new ArrayList<>();
    private List<SubjectTimefoldInstance> subjectList = new ArrayList<>();
    private List<GradeTimefoldInstance> gradeList = new ArrayList<>();
    private List<TimeslotTimefoldInstance> timeslotList = new ArrayList<>();

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TAG;
    }
}
