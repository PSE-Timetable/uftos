package de.uftos.timefold.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.calculator.EasyScoreCalculator;
import de.uftos.timefold.constraints.PredefinedConstraintInstance;
import de.uftos.timefold.domain.TimetableSolutionTimefoldInstance;
import de.uftos.timefold.constraints.ConstraintInstanceAggregate;

import java.util.ArrayList;
import java.util.List;

public class ScoreCalculator implements EasyScoreCalculator<TimetableSolutionTimefoldInstance, HardSoftScore> {

    @Override
    public HardSoftScore calculateScore(TimetableSolutionTimefoldInstance timetableSolutionTimefoldInstance) {
        List<PredefinedConstraintInstance> instances = ConstraintInstanceAggregate.getInstances().stream().filter((instance) -> (instance.evaluate(timetableSolutionTimefoldInstance))).toList();
        System.out.println(instances.size());
        return HardSoftScore.of(instances.size(), 0);
    }
}
