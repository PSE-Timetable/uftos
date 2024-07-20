package de.uftos.solver.timefold.domain.constraintinstances;

/**
 * This interface models a constraint instance, which can be used by the Timefold solver.
 */
public interface ConstraintInstanceTimefoldInstance {
  /**
   * Evaluates the evaluation function with using the parameters of the constraint instance.
   *
   * @return the value to which the evaluation function evaluates to.
   */
  boolean evaluate();
}
