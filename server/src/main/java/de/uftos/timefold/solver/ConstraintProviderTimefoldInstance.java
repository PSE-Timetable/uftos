package de.uftos.timefold.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import de.uftos.timefold.constraints.PredefinedConstraintInstance;

import java.util.ArrayList;
import java.util.List;

public class ConstraintProviderTimefoldInstance implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        List<Constraint> constraints = new ArrayList<>();

        constraints.add(predefinedConstraint(constraintFactory));

        return constraints.toArray(new Constraint[0]);

    }

    Constraint predefinedConstraint(ConstraintFactory constraintFactory) {
        System.out.println(constraintFactory.getClass());
        return constraintFactory.forEach(PredefinedConstraintInstance.class)
                //.filter(PredefinedConstraintInstance::evaluate)
                .penalize(HardSoftScore.ONE_HARD).asConstraint("Predefined");
    }
    //todo: add constraints

}
