package de.uftos.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * This class models a subject that can be used by the Timefold solver.
 */
@Getter
@JsonIdentityInfo(scope = SubjectTimefoldInstance.class,
    generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SubjectTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final String id;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  private final List<TeacherTimefoldInstance> teacherList = new ArrayList<>();
  private final List<StudentTimefoldInstance> lessonList = new ArrayList<>();

  public SubjectTimefoldInstance(String id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.SUBJECT;
  }
}
