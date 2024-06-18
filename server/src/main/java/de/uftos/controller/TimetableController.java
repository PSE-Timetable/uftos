package de.uftos.controller;

import de.uftos.entities.Timetable;
import de.uftos.services.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timetables")
public class TimetableController {
  private final TimetableService timetablesService;

  @Autowired
  public TimetableController(TimetableService timetablesService) {
    this.timetablesService = timetablesService;
  }

  @PostMapping()
  public Timetable createTimetable(@RequestBody Timetable timetables) {
    return this.timetablesService.create(timetables);
  }

  @GetMapping()
  public Page<Timetable> getTimetables(Pageable pageable) {
    return this.timetablesService.get(pageable);
  }

  @GetMapping("/{id}")
  public Timetable getTimetable(@PathVariable String id) {
    return this.timetablesService.getById(id);
  }

  @DeleteMapping("/{id}")
  public void deleteTimetable(@PathVariable String id) {
    this.timetablesService.delete(id);
  }
}
