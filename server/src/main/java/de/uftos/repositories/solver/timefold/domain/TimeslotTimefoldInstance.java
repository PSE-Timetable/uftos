package de.uftos.repositories.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * This class models a timeslot that can be used by the Timefold solver.
 */
@PlanningEntity
@Getter
@Setter
@JsonIdentityInfo(scope = TimeslotTimefoldInstance.class,
    generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TimeslotTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final String id;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  @InverseRelationShadowVariable(sourceVariableName = "timeslot")
  public List<LessonTimefoldInstance> lessonList;
  private int dayOfWeek;
  private int slotId;

  /**
   * Creates a new TimeslotTimefoldInstance.
   *
   * @param id        the id of the timeslot.
   * @param dayOfWeek the day of the timeslot.
   * @param slotId    the slot of the timeslot.
   */
  public TimeslotTimefoldInstance(String id, int dayOfWeek, int slotId) {
    this(id);
    this.dayOfWeek = dayOfWeek;
    this.slotId = slotId;
  }

  public TimeslotTimefoldInstance(String id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.TIMESLOT;
  }
}
