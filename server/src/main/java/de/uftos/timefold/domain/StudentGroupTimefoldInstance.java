package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIdentityInfo(scope = StudentGroupTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class StudentGroupTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final int id;
  private final GradeTimefoldInstance grade;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  private final List<StudentTimefoldInstance> studentList = new ArrayList<>();
  private final List<StudentTimefoldInstance> lessonList = new ArrayList<>();

  public StudentGroupTimefoldInstance(int id, GradeTimefoldInstance grade) {
    this.id = id;
    this.grade = grade;
  }

  public List<LessonTimefoldInstance> getLessonList(List<LessonTimefoldInstance> lessonList) {
    return lessonList.stream().filter(
            (lesson) -> (lesson.getStudentGroup() != null && lesson.getStudentGroup().id == this.id))
        .toList();
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.STUDENT_GROUP;
  }
}
