package de.uftos.controller;

import de.uftos.dto.TimeslotRequestDto;
import de.uftos.entities.Timeslot;
import de.uftos.services.TimeslotService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for the timeslot entity.
 * This controller handles /timeslots HTTP requests.
 */
@RestController
@RequestMapping("/timeslots")
public class TimeslotController {
  private final TimeslotService timeslotsService;

  /**
   * Creates a timeslot controller.
   *
   * @param timeslotsService the service for the timeslot entity.
   */
  @Autowired
  public TimeslotController(TimeslotService timeslotsService) {
    this.timeslotsService = timeslotsService;
  }

  /**
   * Maps the HTTP POST request, to create a new timeslot in the database, to the
   * {@link TimeslotService#create(TimeslotRequestDto) create} function of the teacher service.
   *
   * @param timeslot the teacher which is to be created.
   * @return the created teacher with the assigned ID.
   */
  @PostMapping()
  public Timeslot createTimeslot(@RequestBody TimeslotRequestDto timeslot) {
    return this.timeslotsService.create(timeslot);
  }

  /**
   * Maps the HTTP GET request for a set of timeslots from the database to the
   * {@link TimeslotService#get(Optional) get} function of the timeslot service.
   *
   * @param tags the tags filter.
   * @return the page of timeslots fitting the parameters.
   */
  @GetMapping()
  public List<Timeslot> getTimeslots(Optional<String[]> tags) {
    return this.timeslotsService.get(tags);
  }

  /**
   * Maps the HTTP GET request for a timeslot with the given ID to the
   * {@link TimeslotService#getById(String) getById} function of the timeslot service.
   *
   * @param id the ID of the timeslot.
   * @return the timeslot with the given ID.
   */
  @GetMapping("/{id}")
  public Timeslot getTimeslot(@PathVariable String id) {
    return this.timeslotsService.getById(id);
  }

  /**
   * Maps the HTTP PUT request to update a timeslot to the
   * {@link TimeslotService#update(String, TimeslotRequestDto) update} function of the timeslot
   * service.
   *
   * @param id      the ID of the timeslot which is to be updated.
   * @param timeslot the updated information of the timeslot.
   * @return the updated timeslot.
   */
  @PutMapping("/{id}")
  public Timeslot updateTimeslot(@PathVariable String id,
                                 @RequestBody TimeslotRequestDto timeslot) {
    return this.timeslotsService.update(id, timeslot);
  }

  /**
   * Maps the HTTP DELETE request to the {@link TimeslotService#delete(String) delete} function of
   * the timeslot service.
   *
   * @param id the ID of the timeslot which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteTimeslot(@PathVariable String id) {
    this.timeslotsService.delete(id);
  }
}
