package de.uftos.controller;

import de.uftos.dto.ServerStatisticsResponseDto;
import de.uftos.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
   * @param serverService the service for the student entity.
   */
  @Autowired
  public ServerController(ServerService serverService) {
    this.serverService = serverService;
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

  /**
   * Returns the currently set timeslot length by GET-ting /timeslot-length, using the
   * {@link ServerService#getTimeslotLength()} function of the server service.
   *
   * @return the timeslot length
   */
  @GetMapping("/timeslot-length")
  public int getTimeslotLength() {
    return this.serverService.getTimeslotLength();
  }

  /**
   * Sets the timeslot length by PUT-ting /timeslot-length, using the
   * {@link ServerService#setTimeslotLength(int)} function of the server service.
   *
   * @param length the new timeslot length
   */
  @PutMapping("/timeslot-length")
  public void setTimeslotLength(int length) {
    this.serverService.setTimeslotLength(length);
  }
}
