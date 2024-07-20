package de.uftos.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@PlanningEntity
@Getter
@JsonIdentityInfo(scope = RoomTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RoomTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final int id;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  @InverseRelationShadowVariable(sourceVariableName = "room")
  public List<LessonTimefoldInstance> lessonList;

  public RoomTimefoldInstance(int id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.ROOM;
  }
}
