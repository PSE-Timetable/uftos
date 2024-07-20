package de.uftos.solver.timefold.solver;

import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import java.util.ArrayList;
import java.util.List;

public class ConstraintProviderTimefoldInstance implements ConstraintProvider {
  @Override
  public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
    //todo: add constraints
    List<Constraint> constraints = new ArrayList<>();

    return constraints.toArray(new Constraint[0]);

  }


}
