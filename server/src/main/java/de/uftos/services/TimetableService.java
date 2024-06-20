package de.uftos.services;

import de.uftos.dto.TimetableRequestDto;
import de.uftos.entities.Timetable;
import de.uftos.repositories.database.TimetableRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /timetables endpoint.
 */
@Service
public class TimetableService {
  private final TimetableRepository repository;

  /**
   * Creates a timetable service.
   *
   * @param repository the repository for accessing the timetable table.
   */
  @Autowired
  public TimetableService(TimetableRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the timetable table.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @return the page of entries fitting the parameters.
   */
  public Page<Timetable> get(Pageable pageable, Optional<String> name) {
    return this.repository.findAll(pageable);
  }

  /**
   * Gets a timetable from their ID.
   *
   * @param id the ID of the timetable.
   * @return the timetable with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding timetable.
   */
  public Timetable getById(String id) {
    var timetable = this.repository.findById(id);

    return timetable.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new timetable in the database.
   *
   * @param timetable the information about the timetable which is to be created.
   * @return the created timetable which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the timetable parameter is
   *                                 already present in the database.
   */
  public Timetable create(TimetableRequestDto timetable) {
    // solving magic here
    return null;
  }

  /**
   * Deletes the timetable with the given ID.
   *
   * @param id the ID of the timetable which is to be deleted.
   * @throws ResponseStatusException is thrown if no timetable exists with the given ID.
   */
  public void delete(String id) {
    var timetable = this.repository.findById(id);
    if (timetable.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(timetable.get());
  }
}
