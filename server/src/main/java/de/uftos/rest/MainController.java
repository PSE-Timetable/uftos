package de.uftos.rest;

import de.uftos.demoDataSource.DemoDataSource;
import de.uftos.dto.parser.ConstraintDefinitionDto;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import de.uftos.entities.Timetable;
import de.uftos.solver.TimefoldSolver;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainController {
  private TimetableProblemDto timetable;
  private HashMap<String, ConstraintDefinitionDto> constraints;
  private List<ConstraintInstanceDto> instances;
  private Future<TimetableSolutionDto> solving;
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
  public ResponseEntity<Future<TimetableSolutionDto>> generateTimetable() {
    this.timetable = DemoDataSource.getDemoTimetable();
    //todo: fix data generation
    //this.constraints = DemoDataSource.getPredefinedConstraintDefinitions();
    //this.instances = DemoDataSource.getPredefinedConstraintInstances(this.timetable, this.constraints);
    this.solving = new TimefoldSolver().solve(this.timetable, this.constraints, this.instances);
    return ResponseEntity.ok(this.solving);
  }

  @GetMapping(value = "/status")
  public ResponseEntity<Future<TimetableSolutionDto>> generateStatus() {
    return ResponseEntity.ok(solving);
  }

  @GetMapping(value = "/view")
  public ResponseEntity<TimetableSolutionDto> generateView()
      throws ExecutionException, InterruptedException {
    if (solving == null) {
      return ResponseEntity.ok(null);
    }
    return ResponseEntity.ok(solving.get());
  }

  @GetMapping(value = "/table")
  public ResponseEntity<TimetableProblemDto> generateTable() {
    return ResponseEntity.ok(this.timetable);
  }

}
