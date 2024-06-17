package de.uftos.timefold.constraints;

import de.uftos.demoDataSource.PredefinedConstraints;
import de.uftos.timefold.domain.ResourceTimefoldInstance;
import de.uftos.timefold.domain.TimetableSolutionTimefoldInstance;

import java.util.List;

public record PredefinedConstraintInstance(
        PredefinedConstraints name,
        List<ResourceTimefoldInstance> parameters
) {
    public boolean evaluate(TimetableSolutionTimefoldInstance timetable) {
        try {
            switch (name) {
                case ROOM_CONFLICT -> {
                    return new RoomCollision().getEvaluation(timetable, parameters).call();
                }
                case STUDENT_CONFLICT -> {
                    return new StudentCollision().getEvaluation(timetable, parameters).call();
                }
                case TEACHER_CONFLICT -> {
                    return new TeacherCollision().getEvaluation(timetable, parameters).call();
                }
                case LESSON_VALIDATION -> {
                    return new LessonValidation().getEvaluation(timetable, parameters).call();
                }
                default -> {
                    return false;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
