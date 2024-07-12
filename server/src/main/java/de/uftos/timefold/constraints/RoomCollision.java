package de.uftos.timefold.constraints;

import de.uftos.entities.*;
import de.uftos.timefold.domain.LessonTimefoldInstance;
import de.uftos.timefold.domain.ResourceTimefoldInstance;
import de.uftos.timefold.domain.RoomTimefoldInstance;
import de.uftos.timefold.domain.TimetableSolutionTimefoldInstance;

import java.util.List;
import java.util.concurrent.Callable;

public class RoomCollision implements PredefinedConstraint {
    @Override
    public Callable<Boolean> getEvaluation(TimetableSolutionTimefoldInstance timetable, List<ResourceTimefoldInstance> parameters) {
        if (parameters.size() != 1 || parameters.getFirst().getResourceType() != ResourceType.ROOM) {
            return new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return false;
                }
            };
        }
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                RoomTimefoldInstance room = (RoomTimefoldInstance) parameters.getFirst();
                List<LessonTimefoldInstance> lessons = room.getLessonList(timetable.getLessons());

                for (LessonTimefoldInstance l1 : lessons) {
                    for (LessonTimefoldInstance l2 : lessons) {
                        if (l1 != l2 && l1.getTimeslot() == l2.getTimeslot()) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }
}