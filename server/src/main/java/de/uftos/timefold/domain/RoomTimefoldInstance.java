package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIdentityInfo(scope = RoomTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RoomTimefoldInstance {
    @PlanningId
    private int id;
    private List<Integer> providedTagsIdList;
}
