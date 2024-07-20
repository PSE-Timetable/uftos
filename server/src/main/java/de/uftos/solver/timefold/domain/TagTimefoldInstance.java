package de.uftos.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * This class models a tag that can be used by the Timefold solver.
 */
@Getter
@JsonIdentityInfo(scope = TagTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TagTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final String id;
  private final List<StudentTimefoldInstance> studentList = new ArrayList<>();
  private final List<StudentGroupTimefoldInstance> studentGroupList = new ArrayList<>();
  private final List<TeacherTimefoldInstance> teacherList = new ArrayList<>();
  private final List<RoomTimefoldInstance> roomList = new ArrayList<>();
  private final List<SubjectTimefoldInstance> subjectList = new ArrayList<>();
  private final List<GradeTimefoldInstance> gradeList = new ArrayList<>();
  private final List<TimeslotTimefoldInstance> timeslotList = new ArrayList<>();

  public TagTimefoldInstance(String id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.TAG;
  }
}
