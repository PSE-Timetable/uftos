package de.uftos.repositories.solver;

import de.uftos.dto.parser.ConstraintDefinitionDto;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The solver repository provides functionality for solving timetable scheduling problems.
 */
public class SolverRepositoryImpl implements SolverRepository {
  @Override
  public Future<TimetableSolutionDto> solve(TimetableProblemDto timetable,
                                            HashMap<String, ConstraintDefinitionDto> constaintDefinitions,
                                            List<ConstraintInstanceDto> constraintInstances)
      throws IllegalArgumentException {
    return null;
  }
}
