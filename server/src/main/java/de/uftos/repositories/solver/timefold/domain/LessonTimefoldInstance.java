package de.uftos.repositories.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import de.uftos.dto.ResourceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class models a lesson that can be used by the Timefold solver.
 */
@Getter
@Setter
@PlanningEntity
@NoArgsConstructor
public class LessonTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private String id;

  private int index;

  @JsonIdentityReference
  @PlanningVariable
  private TimeslotTimefoldInstance timeslot;

  @JsonIdentityReference
  @PlanningVariable
  private TeacherTimefoldInstance teacher;

  @JsonIdentityReference
  private StudentGroupTimefoldInstance studentGroup;

  @JsonIdentityReference
  private SubjectTimefoldInstance subject;

  @JsonIdentityReference
  @PlanningVariable
  private RoomTimefoldInstance room;

  public LessonTimefoldInstance(String id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.LESSON;
  }
}
