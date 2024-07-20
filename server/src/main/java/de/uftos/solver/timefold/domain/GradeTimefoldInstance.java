package de.uftos.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIdentityInfo(scope = GradeTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GradeTimefoldInstance implements ResourceTimefoldInstance {
  @PlanningId
  private final String id;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  private final List<StudentGroupTimefoldInstance> studentGroupList = new ArrayList<>();

  public GradeTimefoldInstance(String id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.GRADE;
  }
}
