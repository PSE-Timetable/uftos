package de.uftos.controller;

import de.uftos.dto.ServerStatisticsResponseDto;
import de.uftos.services.ServerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server")
public class ServerController {
  private ServerService serverService;

  @GetMapping("/statistics")
  public ServerStatisticsResponseDto getServerStats() {
    return this.serverService.getStats();
  }

  @GetMapping("/timeslot-length")
  public int getTimeslotLength() {
    return this.serverService.getTimeslotLength();
  }

  @PutMapping("/timeslot-length")
  public void setTimeslotLength(int length) {
    this.serverService.setTimeslotLength(length);
  }
}
