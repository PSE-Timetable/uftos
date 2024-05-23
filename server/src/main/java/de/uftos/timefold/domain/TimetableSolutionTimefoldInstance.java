package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class TimetableSolutionTimefoldInstance {
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<GradeTimefoldInstance> grades;
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<RoomTimefoldInstance> rooms;
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<StudentGroupTimefoldInstance> studentGroups;
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<StudentTimefoldInstance> students;
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<SubjectTimefoldInstance> subjects;
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<TeacherTimefoldInstance> teachers;
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private List<TimeslotTimefoldInstance> timeslots;

    @PlanningEntityCollectionProperty
    private List<LessonTimefoldInstance> lessons;

    @PlanningScore
    private HardSoftScore score;

}
