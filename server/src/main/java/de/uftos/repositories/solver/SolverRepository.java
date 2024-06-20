package de.uftos.repositories.solver;

import de.uftos.entities.Timetable;
import java.util.HashMap;
import java.util.concurrent.Future;

public interface SolverRepository {
  /**
   * Solves the scheduling problem for the given timetable problem instance.
   * @param timetable the problem instance which needs to be solved.
   * @param constaintDefinitions the definitions of all constraints.
   * @param constraintInstances instances of the constraint definitions which need to be satisfied.
   * @return a Future which will provide the solved timetable.
   * @throws IllegalArgumentException when the constraint instances do not fit the resources of the timetable or constraint definitions.
   */
  //Future<Timetable> solve(Timetable timetable, HashMap<String, ConstraintDefinition> constaintDefinitions, List<ConstraintInstance> constraintInstances) throws IllegalArgumentException;
}
