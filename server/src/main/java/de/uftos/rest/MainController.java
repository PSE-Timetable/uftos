package de.uftos.rest;

import de.uftos.demoDataSource.DemoDataSource;
import de.uftos.demoDataSource.PredefinedConstraint;
import de.uftos.demoDataSource.PredefinedConstraintInstance;
import de.uftos.demoDataSource.PredefinedConstraints;
import de.uftos.entities.Timetable;
import de.uftos.solver.TimefoldSolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/main")
public class MainController {
    private Timetable timetable;
    private HashMap<PredefinedConstraints, PredefinedConstraint> constraints;
    private List<PredefinedConstraintInstance> instances;
    private Future<Timetable> solving;
    private Timetable solution;
    @GetMapping()
    public ResponseEntity<String> generateMain() {
        return ResponseEntity.ok("Main");
    }
    @GetMapping(value = "/test")
    public ResponseEntity<String> generateTest() {
        return ResponseEntity.ok("test");
    }
    @GetMapping(value = "/start")
    public ResponseEntity<Future<Timetable>> generateTimetable() {
        this.timetable = DemoDataSource.getDemoTimetable();
        this.constraints = DemoDataSource.getPredefinedConstraintDefinitions();
        this.instances = DemoDataSource.getPredefinedConstraintInstances(this.timetable, this.constraints);
        this.solving = new TimefoldSolver().solve(this.constraints, this.instances, this.timetable);
        return ResponseEntity.ok(this.solving);
    }
    @GetMapping(value = "/status")
    public ResponseEntity<Future<Timetable>> generateStatus() {
        return ResponseEntity.ok(solving);
    }
    @GetMapping(value = "/view")
    public ResponseEntity<Timetable> generateView() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(solving.get());
    }
    @GetMapping(value = "/table")
    public ResponseEntity<Timetable> generateTable() {
        return ResponseEntity.ok(this.timetable);
    }

}
