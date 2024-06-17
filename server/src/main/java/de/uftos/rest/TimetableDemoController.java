package de.uftos.rest;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import ai.timefold.solver.core.impl.solver.DefaultSolverFactory;
import de.uftos.demoDataSource.DemoDataSource;
import de.uftos.demoDataSource.PredefinedConstraint;
import de.uftos.demoDataSource.PredefinedConstraintInstance;
import de.uftos.demoDataSource.PredefinedConstraints;
import de.uftos.entities.*;
import de.uftos.solver.TimefoldSolver;
import de.uftos.timefold.domain.*;
import de.uftos.timefold.solver.ConstraintProviderTimefoldInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/demo-data")
public class TimetableDemoController {

    @GetMapping()
    public ResponseEntity<Timetable> generateDemo() {
        return ResponseEntity.ok(DemoDataSource.getDemoTimetable());
    }

    @GetMapping(value = "/solved")
    public ResponseEntity<Timetable> generateSolution() throws ExecutionException, InterruptedException {

        SolverConfig solverConfig = new SolverConfig()
                .withTerminationConfig(new TerminationConfig()
                .withMillisecondsSpentLimit(5000L))
                .withSolutionClass(TimetableSolutionTimefoldInstance.class)
                .withEntityClassList(Arrays.stream(new Class<?>[]{LessonTimefoldInstance.class}).toList());
        solverConfig.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig().withConstraintProviderClass(ConstraintProviderTimefoldInstance.class));

        SolverFactory<TimetableSolutionTimefoldInstance> factory = new DefaultSolverFactory<>(solverConfig);

        Solver<TimetableSolutionTimefoldInstance> solver = factory.buildSolver();

        Timetable timetable = DemoDataSource.getDemoTimetable();
        HashMap<PredefinedConstraints, PredefinedConstraint> constraints = DemoDataSource.getPredefinedConstraintDefinitions();
        List<PredefinedConstraintInstance> instances = DemoDataSource.getPredefinedConstraintInstances(timetable, constraints);

        return ResponseEntity.ok(new TimefoldSolver().solve(constraints, instances, timetable).get());
    }

    @GetMapping(value = "/test")
    public ResponseEntity<String> generateTest() {
        return ResponseEntity.ok("test");
    }
}