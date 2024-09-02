package de.uftos.controller;

import de.uftos.dto.responsedtos.ServerStatisticsResponseDto;
import de.uftos.entities.TimetableMetadata;
import de.uftos.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for everything server.
 * This controller handles /server HTTP requests.
 */
@RestController
@RequestMapping("/server")
public class ServerController {
  private final ServerService serverService;

  /**
   * Creates the server controller.
   *
   * @param serverService the service for the server entity.
   */
  @Autowired
  public ServerController(ServerService serverService) {
    this.serverService = serverService;
  }

  /**
   * Returns the currently set timeslot length by GET-ting /timeslot-length, using the
   * {@link ServerService#getTimetableMetadata()} function of the server service.
   *
   * @return the timeslot length
   */
  @GetMapping("/timetable-metadata")
  public TimetableMetadata getTimetableMetadata() {
    return this.serverService.getTimetableMetadata();
  }

  /**
   * Sets the timeslot length by PUT-ting /timeslot-length, using the
   * {@link ServerService#setTimetableMetadata(TimetableMetadata)} function of the server service.
   *
   * @param timetableMetadata the new timetable metadata
   */
  @PutMapping("/timetable-metadata")
  public void setTimetableMetadata(@RequestBody TimetableMetadata timetableMetadata) {
    this.serverService.setTimetableMetadata(timetableMetadata);
  }

  /**
   * Returns server statistics by GET-ting /statistics, using the
   * {@link ServerService#getStats()} function of the server service.
   *
   * @return statistics about the server; counts of various resources
   */
  @GetMapping("/statistics")
  public ServerStatisticsResponseDto getServerStats() {
    return this.serverService.getStats();
  }
}
