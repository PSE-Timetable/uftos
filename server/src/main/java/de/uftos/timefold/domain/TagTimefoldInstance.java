package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIdentityInfo(scope = TagTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TagTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final int id;
  private final List<StudentTimefoldInstance> studentList = new ArrayList<>();
  private final List<StudentGroupTimefoldInstance> studentGroupList = new ArrayList<>();
  private final List<TeacherTimefoldInstance> teacherList = new ArrayList<>();
  private final List<RoomTimefoldInstance> roomList = new ArrayList<>();
  private final List<SubjectTimefoldInstance> subjectList = new ArrayList<>();
  private final List<GradeTimefoldInstance> gradeList = new ArrayList<>();
  private final List<TimeslotTimefoldInstance> timeslotList = new ArrayList<>();
  public TagTimefoldInstance(int id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.TAG;
  }
}
