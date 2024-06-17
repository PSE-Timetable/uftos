package de.uftos.timefold.constraints;

import de.uftos.entities.*;
import de.uftos.timefold.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StudentCollision implements PredefinedConstraint {
    @Override
    public Callable<Boolean> getEvaluation(TimetableSolutionTimefoldInstance timetable, List<ResourceTimefoldInstance> parameters) {
        if (parameters.size() != 1 || parameters.getFirst().getResourceType() != ResourceType.STUDENT) {
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
                StudentTimefoldInstance student = (StudentTimefoldInstance) parameters.getFirst();
                List<StudentGroupTimefoldInstance> studentGroups = student.getStudentGroupList();

                List<LessonTimefoldInstance> lessons = new ArrayList<>();

                lessonLoop: for (LessonTimefoldInstance l : timetable.getLessons()) {
                    for (StudentGroupTimefoldInstance sg : studentGroups) {
                        if (l.getStudentGroup().getId() == sg.getId()) {
                            lessons.add(l);
                            continue lessonLoop;
                        }
                    }
                }

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
