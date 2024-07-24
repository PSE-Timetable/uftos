package de.uftos.repositories.solver.timefold.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceHardPenalize;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceHardReward;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceSoftPenalize;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceSoftReward;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the constraint instances to the Timefold solver.
 */
public class ConstraintProviderTimefoldInstance implements ConstraintProvider {
  @Override
  public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
    List<Constraint> constraints = new ArrayList<>();

    constraints.add(hardPenalize(constraintFactory));
    constraints.add(softPenalize(constraintFactory));
    constraints.add(hardReward(constraintFactory));
    constraints.add(softReward(constraintFactory));

    return constraints.toArray(new Constraint[0]);

  }

  private Constraint hardPenalize(ConstraintFactory constraintFactory) {
    return constraintFactory.forEach(ConstraintInstanceHardPenalize.class)
        .filter(ConstraintInstanceHardPenalize::evaluate)
        .penalize(HardSoftScore.ONE_HARD)
        .asConstraint("hard-penalize");
  }

  private Constraint softPenalize(ConstraintFactory constraintFactory) {
    return constraintFactory.forEach(ConstraintInstanceSoftPenalize.class)
        .filter(ConstraintInstanceSoftPenalize::evaluate)
        .penalize(HardSoftScore.ONE_SOFT)
        .asConstraint("soft-penalize");
  }

  private Constraint hardReward(ConstraintFactory constraintFactory) {
    return constraintFactory.forEach(ConstraintInstanceHardReward.class)
        .filter(ConstraintInstanceHardReward::evaluate)
        .reward(HardSoftScore.ONE_HARD)
        .asConstraint("hard-reward");
  }

  private Constraint softReward(ConstraintFactory constraintFactory) {
    return constraintFactory.forEach(ConstraintInstanceSoftReward.class)
        .filter(ConstraintInstanceSoftReward::evaluate)
        .reward(HardSoftScore.ONE_SOFT)
        .asConstraint("soft-reward");
  }
}
