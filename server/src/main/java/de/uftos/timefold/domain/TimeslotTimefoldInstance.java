package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.timefoldQuickstart.domain.Room;
import lombok.Getter;

@Getter
@JsonIdentityInfo(scope = TimeslotTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TimeslotTimefoldInstance {
    @PlanningId
    private int id;
    private int dayId;
    private int slotId;
}
