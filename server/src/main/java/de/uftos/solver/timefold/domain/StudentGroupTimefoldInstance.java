package de.uftos.solver.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * This class models a student group that can be used by the Timefold solver.
 */
@Getter
@Setter
@JsonIdentityInfo(scope = StudentGroupTimefoldInstance.class,
    generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class StudentGroupTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final String id;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  private final List<StudentTimefoldInstance> studentList = new ArrayList<>();
  private final List<StudentTimefoldInstance> lessonList = new ArrayList<>();
  private GradeTimefoldInstance grade;

  public StudentGroupTimefoldInstance(String id) {
    this.id = id;
  }

  public StudentGroupTimefoldInstance(String id, GradeTimefoldInstance grade) {
    this(id);
    this.grade = grade;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.STUDENT_GROUP;
  }
}
