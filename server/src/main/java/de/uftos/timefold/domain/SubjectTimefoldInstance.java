package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.timefoldQuickstart.domain.Room;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIdentityInfo(scope = SubjectTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SubjectTimefoldInstance {
    @PlanningId
    private int id;
    private List<Integer> providedTagsIdList;
}
