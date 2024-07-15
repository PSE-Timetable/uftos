package de.uftos.timefold.domain;

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
@JsonIdentityInfo(scope = TimeslotTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TimeslotTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final int id;
  private final int dayOfWeek;
  private final int slotId;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  @InverseRelationShadowVariable(sourceVariableName = "timeslot")
  public List<LessonTimefoldInstance> lessonList;

  public TimeslotTimefoldInstance(int id, int dayOfWeek, int slotId) {
    this.id = id;
    this.dayOfWeek = dayOfWeek;
    this.slotId = slotId;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.TIMESLOT;
  }
}