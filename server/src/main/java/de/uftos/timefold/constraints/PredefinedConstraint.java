package de.uftos.timefold.constraints;

import de.uftos.timefold.domain.ResourceTimefoldInstance;
import de.uftos.timefold.domain.TimetableSolutionTimefoldInstance;

import java.util.List;
import java.util.concurrent.Callable;

//only for demo-data usage
public interface PredefinedConstraint {
    Callable<Boolean> getEvaluation(TimetableSolutionTimefoldInstance timetable, List<ResourceTimefoldInstance> parameters);
}
