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

/**
 * This class models a teacher that can be used by the Timefold solver.
 */
@PlanningEntity
@Getter
@JsonIdentityInfo(scope = TeacherTimefoldInstance.class,
    generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TeacherTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final String id;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  private final List<SubjectTimefoldInstance> subjectList = new ArrayList<>();
  @InverseRelationShadowVariable(sourceVariableName = "teacher")
  public List<LessonTimefoldInstance> lessonList;

  public TeacherTimefoldInstance(String id) {
    this.id = id;
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.TEACHER;
  }
}
