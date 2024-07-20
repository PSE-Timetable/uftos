package de.uftos.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import de.uftos.dto.ResourceType;
import de.uftos.solver.timefold.domain.constraintinstances.ConstraintInstanceTimefoldInstance;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * This class models a timetable problem instance that can be used by the Timefold solver.
 */
@PlanningSolution
public class TimetableSolutionTimefoldInstance implements ResourceTimefoldInstance {

  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<GradeTimefoldInstance> grades = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<RoomTimefoldInstance> rooms = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<StudentGroupTimefoldInstance> studentGroups = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<StudentTimefoldInstance> students = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<SubjectTimefoldInstance> subjects = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<TeacherTimefoldInstance> teachers = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<TimeslotTimefoldInstance> timeslots = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<TagTimefoldInstance> tags = new ArrayList<>();
  @PlanningEntityCollectionProperty
  @Getter
  private List<LessonTimefoldInstance> lessons = new ArrayList<>();
  @ProblemFactCollectionProperty
  @ValueRangeProvider
  @Getter
  private List<ConstraintInstanceTimefoldInstance> constraintInstances = new ArrayList<>();
  @PlanningScore
  @Getter
  private HardSoftScore score;

  public TimetableSolutionTimefoldInstance() {
  }

  public TimetableSolutionTimefoldInstance(
      List<GradeTimefoldInstance> grades,
      List<RoomTimefoldInstance> rooms,
      List<StudentGroupTimefoldInstance> studentGroups,
      List<StudentTimefoldInstance> students,
      List<SubjectTimefoldInstance> subjects,
      List<TeacherTimefoldInstance> teachers,
      List<TimeslotTimefoldInstance> timeslots,
      List<TagTimefoldInstance> tags,
      List<LessonTimefoldInstance> lessons,
      List<ConstraintInstanceTimefoldInstance> constraintInstances
  ) {
    this.grades = grades;
    this.rooms = rooms;
    this.studentGroups = studentGroups;
    this.students = students;
    this.subjects = subjects;
    this.teachers = teachers;
    this.timeslots = timeslots;
    this.tags = tags;
    this.lessons = lessons;
    this.constraintInstances = constraintInstances;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.TIMETABLE;
  }
}
