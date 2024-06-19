package de.uftos.controller;

import de.uftos.dto.TimeslotRequestDto;
import de.uftos.entities.Timeslot;
import de.uftos.services.TimeslotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeslots")
public class TimeslotController {
  private final TimeslotService timeslotsService;

  @Autowired
  public TimeslotController(TimeslotService timeslotsService) {
    this.timeslotsService = timeslotsService;
  }

  @PostMapping()
  public Timeslot createTimeslot(@RequestBody TimeslotRequestDto timeslot) {
    return this.timeslotsService.create(timeslot);
  }

  @GetMapping()
  public Page<Timeslot> getTimeslots(Pageable pageable) {
    return this.timeslotsService.get(pageable);
  }

  @GetMapping("/{id}")
  public Timeslot getTimeslot(@PathVariable String id) {
    return this.timeslotsService.getById(id);
  }

  @PutMapping("/{id}")
  public Timeslot updateTimeslot(@PathVariable String id,
                                 @RequestBody TimeslotRequestDto timeslot) {
    return this.timeslotsService.update(id, timeslot);
  }

  @DeleteMapping("/{id}")
  public void deleteTimeslot(@PathVariable String id) {
    this.timeslotsService.delete(id);
  }
}
