package de.uftos.timefold.constraints;

import de.uftos.entities.ResourceType;
import de.uftos.timefold.domain.LessonTimefoldInstance;
import de.uftos.timefold.domain.ResourceTimefoldInstance;
import de.uftos.timefold.domain.TimetableSolutionTimefoldInstance;

import java.util.List;
import java.util.concurrent.Callable;

public class LessonValidation implements PredefinedConstraint {
    @Override
    public Callable<Boolean> getEvaluation(TimetableSolutionTimefoldInstance timetable, List<ResourceTimefoldInstance> parameters) {
        if (parameters.size() != 1 || parameters.getFirst().getResourceType() != ResourceType.LESSON) {
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
                LessonTimefoldInstance lesson = timetable.getLessons().get(((LessonTimefoldInstance) parameters.getFirst()).getId());

                if (lesson.getTimeslot() == null
                        || lesson.getTeacher() == null
                        || lesson.getRoom() == null
                        || lesson.getStudentGroup() == null
                        || lesson.getSubject() == null
                ) {
                    return true;
                }

                return false;
            }
        };
    }
}
