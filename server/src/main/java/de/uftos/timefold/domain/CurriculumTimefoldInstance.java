package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.timefoldQuickstart.domain.Room;
import lombok.Getter;

@Getter
@JsonIdentityInfo(scope = CurriculumTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CurriculumTimefoldInstance {
    @PlanningId
    private int id;
    private int subjectId;
    private int gradeId;
    private int amountOfLessonsPerWeek;
}
