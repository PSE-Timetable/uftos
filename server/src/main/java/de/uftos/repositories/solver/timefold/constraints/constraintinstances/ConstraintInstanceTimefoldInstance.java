package de.uftos.repositories.solver.timefold.constraints.constraintinstances;

import de.uftos.dto.solver.RewardPenalize;
import de.uftos.repositories.solver.timefold.domain.TimetableSolutionTimefoldInstance;

/**
 * This interface models a constraint instance, which can be used by the Timefold solver.
 */
public interface ConstraintInstanceTimefoldInstance {
  /**
   * Evaluates the evaluation function with using the parameters of the constraint instance.
   *
   * @return the value to which the evaluation function evaluates to.
   */
  boolean evaluate(TimetableSolutionTimefoldInstance timetable);

  RewardPenalize rewardPenalize();
}
