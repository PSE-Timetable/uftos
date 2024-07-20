package de.uftos.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIdentityInfo(scope = StudentGroupTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class StudentTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final int id;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  private final List<StudentGroupTimefoldInstance> studentGroupList = new ArrayList<>();

  public StudentTimefoldInstance(int id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.STUDENT;
  }
}
