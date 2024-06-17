package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.entities.ResourceType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIdentityInfo(scope = TeacherTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TeacherTimefoldInstance implements ResourceTimefoldInstance{

    public TeacherTimefoldInstance(int id) {
        this.id = id;
    }
    @PlanningId
    private int id;
    private List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
    private List<SubjectTimefoldInstance> subjectList = new ArrayList<>();

    public List<LessonTimefoldInstance> getLessonList(List<LessonTimefoldInstance> lessonList) {
        return lessonList.stream().filter((lesson) -> (lesson.getTeacher() != null && lesson.getTeacher().id == this.id)).toList();
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TEACHER;
    }
}
