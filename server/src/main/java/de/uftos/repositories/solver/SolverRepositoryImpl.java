package de.uftos.repositories.solver;

import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import org.springframework.stereotype.Repository;

/**
 * The solver repository provides functionality for solving timetable scheduling problems.
 */
@Repository
public class SolverRepositoryImpl implements SolverRepository {
  public Future<TimetableSolutionDto> solve(TimetableProblemDto timetable,
                                            HashMap<String, ConstraintDefinitionDto> constraintDefinitions,
                                            List<ConstraintInstanceDto> constraintInstances)
      throws IllegalArgumentException {
    return null;
  }
}
