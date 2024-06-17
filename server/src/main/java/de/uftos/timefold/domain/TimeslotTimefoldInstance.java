package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.entities.ResourceType;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIdentityInfo(scope = TimeslotTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TimeslotTimefoldInstance implements ResourceTimefoldInstance {

    public TimeslotTimefoldInstance (int id, int dayOfWeek, int slotId) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.slotId = slotId;
    }

    @PlanningId
    private int id;
    private int dayOfWeek;
    private int slotId;
    private List<TagTimefoldInstance> providedTagsList = new ArrayList<>();


    public List<LessonTimefoldInstance> getLessonList(List<LessonTimefoldInstance> lessonList) {
        return lessonList.stream().filter((lesson) -> (lesson.getTimeslot() != null && lesson.getTimeslot().id == this.id)).toList();
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.TIMESLOT;
    }
}
