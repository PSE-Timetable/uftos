package de.uftos.repositories.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import de.uftos.dto.ResourceType;
import lombok.Getter;
import lombok.Setter;

/**
 * This class models a lesson that can be used by the Timefold solver.
 */
@Getter
@Setter
@PlanningEntity
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

  //required by the timefold solver
  public LessonTimefoldInstance() {

  }

  /**
   * Creates a new LessonTimefoldInstance.
   *
   * @param id           the UUID of the lesson.
   * @param index        the index of the lesson.
   * @param subject      the subject of the lesson.
   * @param studentGroup the student group of the lesson.
   */
  public LessonTimefoldInstance(String id, int index, SubjectTimefoldInstance subject,
                                StudentGroupTimefoldInstance studentGroup) {
    this(id);
    this.index = index;
    this.subject = subject;
    this.studentGroup = studentGroup;
  }

  public LessonTimefoldInstance(String id, int index, SubjectTimefoldInstance subject,
                                TeacherTimefoldInstance teacher,
                                StudentGroupTimefoldInstance studentGroup) {
    this(id, index, subject, studentGroup);
    this.teacher = teacher;
  }

  public LessonTimefoldInstance(String id) {
    this.id = id;
  }

  /**
   * Creates a new LessonTimefoldInstance.
   *
   * @param id           the UUID of the lesson.
   * @param index        the index of the lesson.
   * @param timeslot     the timeslot of the lesson.
   * @param teacher      the teacher of the lesson.
   * @param studentGroup the student group of the lesson.
   * @param subject      the subject of the lesson.
   * @param room         the room of the lesson.
   */
  public LessonTimefoldInstance(String id, int index, TimeslotTimefoldInstance timeslot,
                                TeacherTimefoldInstance teacher,
                                StudentGroupTimefoldInstance studentGroup,
                                SubjectTimefoldInstance subject, RoomTimefoldInstance room) {
    this(id, index, subject, teacher, studentGroup);
    this.timeslot = timeslot;
    this.room = room;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.LESSON;
  }
}
