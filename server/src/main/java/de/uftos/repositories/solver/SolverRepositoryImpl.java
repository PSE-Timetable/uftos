package de.uftos.repositories.solver;

import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

/**
 * This class is only required for spring-magic and forwards the method call to TimefoldSolver.
 */
public class SolverRepositoryImpl implements SolverRepository {
  @Override
  public Future<TimetableSolutionDto> solve(TimetableProblemDto timetable,
                                            HashMap<String, ConstraintDefinitionDto> constaintDefinitions,
                                            List<ConstraintInstanceDto> constraintInstances)
      throws IllegalArgumentException {
    return new TimefoldSolver().solve(timetable, constaintDefinitions, constraintInstances);
  }
}
