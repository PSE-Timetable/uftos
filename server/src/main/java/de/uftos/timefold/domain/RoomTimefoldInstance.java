package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIdentityInfo(scope = RoomTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RoomTimefoldInstance implements ResourceTimefoldInstance {

  @PlanningId
  private final int id;
  private final List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
  public RoomTimefoldInstance(int id) {
    this.id = id;
  }

  public List<LessonTimefoldInstance> getLessonList(List<LessonTimefoldInstance> lessonList) {
    return lessonList.stream()
        .filter((lesson) -> (lesson.getRoom() != null && lesson.getRoom().id == this.id)).toList();
  }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.ROOM;
  }
}
