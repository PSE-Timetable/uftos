package de.uftos.solver;

import de.uftos.demoDataSource.PredefinedConstraint;
import de.uftos.demoDataSource.PredefinedConstraintInstance;
import de.uftos.demoDataSource.PredefinedConstraints;
import de.uftos.dataTransferObjects.ConstraintDefinition;
import de.uftos.dataTransferObjects.ConstraintInstance;
import de.uftos.entities.Timetable;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

public interface SolverAdapter {
    Future<Timetable> solve(Timetable timetable, HashMap<String, ConstraintDefinition> definitions, List<ConstraintInstance> instances);
    Future<Timetable> solve(HashMap<PredefinedConstraints, PredefinedConstraint> definitions, List<PredefinedConstraintInstance> instances, Timetable timetable);
}
