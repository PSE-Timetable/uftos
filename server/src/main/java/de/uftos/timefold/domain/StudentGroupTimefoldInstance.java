package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.entities.ResourceType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIdentityInfo(scope = StudentGroupTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class StudentGroupTimefoldInstance implements ResourceTimefoldInstance {

    public StudentGroupTimefoldInstance(int id, GradeTimefoldInstance grade) {
        this.id = id;
        this.grade = grade;
    }

    @PlanningId
    private int id;
    private GradeTimefoldInstance grade;
    private List<TagTimefoldInstance> providedTagsList = new ArrayList<>();
    private List<StudentTimefoldInstance> studentList = new ArrayList<>();

    public List<LessonTimefoldInstance> getLessonList(List<LessonTimefoldInstance> lessonList) {
        return lessonList.stream().filter((lesson) -> (lesson.getStudentGroup() != null && lesson.getStudentGroup().id == this.id)).toList();
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.STUDENT_GROUP;
    }
}
