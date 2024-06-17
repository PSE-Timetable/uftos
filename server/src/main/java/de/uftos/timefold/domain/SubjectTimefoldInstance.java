package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.entities.ResourceType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIdentityInfo(scope = SubjectTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SubjectTimefoldInstance implements ResourceTimefoldInstance {

    public SubjectTimefoldInstance (int id) {
        this.id = id;
    }
    @PlanningId
    private int id;
    private List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
    private List<TeacherTimefoldInstance> teacherList = new ArrayList<>();

    public List<LessonTimefoldInstance> getLessonList(List<LessonTimefoldInstance> lessonList) {
        return lessonList.stream().filter((lesson) -> (lesson.getSubject() != null && lesson.getSubject().id == this.id)).toList();
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SUBJECT;
    }
}
