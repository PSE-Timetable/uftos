package de.uftos.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import de.uftos.dto.ResourceType;
import de.uftos.solver.timefold.constraints.constraintinstances.ConstraintInstanceTimefoldInstance;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * This class models a timetable problem instance that can be used by the Timefold solver.
 */
@Getter
@PlanningSolution
public class TimetableSolutionTimefoldInstance implements ResourceTimefoldInstance {

  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<GradeTimefoldInstance> grades = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<RoomTimefoldInstance> rooms = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<StudentGroupTimefoldInstance> studentGroups = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<StudentTimefoldInstance> students = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<SubjectTimefoldInstance> subjects = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<TeacherTimefoldInstance> teachers = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<TimeslotTimefoldInstance> timeslots = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<TagTimefoldInstance> tags = new ArrayList<>();
  @PlanningEntityCollectionProperty
  private final List<LessonTimefoldInstance> lessons = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  private final List<ConstraintInstanceTimefoldInstance> constraintInstances = new ArrayList<>();
  @PlanningScore
  private HardSoftScore score;

  public TimetableSolutionTimefoldInstance() {
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.TIMETABLE;
  }
}
