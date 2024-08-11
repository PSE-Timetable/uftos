package de.uftos.repositories.solver;

import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * The solver repository provides functionality for solving timetable scheduling problems.
 */
public interface SolverRepository {
  /**
   * Solves the scheduling problem for the given timetable problem instance.
   *
   * @param timetable           the problem instance which needs to be solved.
   * @param solverFinishedEvent the event triggered using the solution, once the solver finished.
   * @return a Future which will provide the solved timetable.
   * @throws IllegalArgumentException when the constraint instances do not fit the resources of the
   *                                  timetable or constraint definitions.
   */
  Future<TimetableSolutionDto> solve(TimetableProblemDto timetable,
                                     Consumer<TimetableSolutionDto> solverFinishedEvent)
      throws IllegalArgumentException;
}
