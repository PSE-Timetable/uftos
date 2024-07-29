package de.uftos.controller;

import de.uftos.dto.requestdtos.TimetableRequestDto;
import de.uftos.entities.Timetable;
import de.uftos.services.TimetableService;
import java.util.Optional;
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

/**
 * The REST controller for the timetable entity.
 * This controller handles /timetables HTTP requests.
 */
@RestController
@RequestMapping("/timetables")
public class TimetableController {
  private final TimetableService timetablesService;

  /**
   * Creates a timetable controller.
   *
   * @param timetablesService the service for the timetable entity.
   */
  @Autowired
  public TimetableController(TimetableService timetablesService) {
    this.timetablesService = timetablesService;
  }

  /**
   * Maps the HTTP POST request, to create a new timetable in the database, to the
   * {@link TimetableService#create(TimetableRequestDto) create} function of the timetable service.
   *
   * @param timetable the timetable which is to be created.
   * @return the created timetable with the assigned ID.
   */
  @PostMapping()
  public Timetable createTimetable(@RequestBody TimetableRequestDto timetable) {
    return this.timetablesService.create(timetable);
  }

  /**
   * Maps the HTTP GET request for a set of timetables from the database to the
   * {@link TimetableService#get(Pageable, Optional) get} function of the timetable service.
   *
   * @param pageable contains the parameters for the page.
   * @param name     optional name to filter for.
   * @return the page of timetable fitting the parameters.
   */
  @GetMapping()
  public Page<Timetable> getTimetables(Pageable pageable, Optional<String> name) {
    return this.timetablesService.get(pageable, name);
  }

  /**
   * Maps the HTTP GET request for a timetable with the given ID to the
   * {@link TimetableService#getById(String) getById} function of the timetable service.
   *
   * @param id the ID of the timetable.
   * @return the timetable with the given ID.
   */
  @GetMapping("/{id}")
  public Timetable getTimetable(@PathVariable String id) {
    return this.timetablesService.getById(id);
  }

  /**
   * Maps the HTTP DELETE request to the {@link TimetableService#delete(String) delete} function of
   * the timetable service.
   *
   * @param id the ID of the timetable which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteTimetable(@PathVariable String id) {
    this.timetablesService.delete(id);
  }
}
