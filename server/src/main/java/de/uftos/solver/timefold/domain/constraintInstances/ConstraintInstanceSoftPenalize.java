package de.uftos.solver.timefold.domain.constraintInstances;

import de.uftos.solver.timefold.domain.ResourceTimefoldInstance;
import java.util.List;
import java.util.function.Function;

public record ConstraintInstanceSoftPenalize(
    List<ResourceTimefoldInstance> parameters,
    Function<List<ResourceTimefoldInstance>, Boolean> evaluationFunction
) implements ConstraintInstanceTimefoldInstance {
  public boolean evaluate() {
    return evaluationFunction.apply(parameters);
  }
}
