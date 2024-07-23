package de.uftos.services;

import de.uftos.dto.TimeslotRequestDto;
import de.uftos.entities.Timeslot;
import de.uftos.repositories.database.TimeslotRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /timeslots endpoint.
 */
@Service
public class TimeslotService {
  private final TimeslotRepository repository;

  /**
   * Creates a timeslot service.
   *
   * @param repository the repository for accessing the timeslot table.
   */
  @Autowired
  public TimeslotService(TimeslotRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the timeslot table.
   *
   * @return all timeslots.
   */
  public List<Timeslot> get() {
    return this.repository.findAll();
  }

  /**
   * Gets a timeslot from their ID.
   *
   * @param id the ID of the timeslot.
   * @return the timeslot with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding timeslot.
   */
  public Timeslot getById(String id) {
    var timeslot = this.repository.findById(id);

    return timeslot.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new timeslot in the database.
   *
   * @param timeslot the information about the timeslot which is to be created.
   * @return the created timeslot which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the timeslot parameter is
   *                                 already present in the database.
   */
  public Timeslot create(TimeslotRequestDto timeslot) {
    return this.repository.save(timeslot.map());
  }

  /**
   * Updates the timeslot with the given ID.
   *
   * @param id              the ID of the timeslot which is to be updated.
   * @param timeslotRequest the updated timeslot information.
   * @return the updated timeslot.
   */
  public Timeslot update(String id, TimeslotRequestDto timeslotRequest) {
    Timeslot timeslot = timeslotRequest.map();
    timeslot.setId(id);

    return this.repository.save(timeslot);
  }

  /**
   * Deletes the timeslot with the given ID.
   *
   * @param id the ID of the timeslot which is to be deleted.
   * @throws ResponseStatusException is thrown if no timeslot exists with the given ID.
   */
  public void delete(String id) {
    var timeslot = this.repository.findById(id);
    if (timeslot.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(timeslot.get());
  }
}
