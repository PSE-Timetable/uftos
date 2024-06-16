package de.uftos.services;

import de.uftos.entities.Timeslot;
import de.uftos.repositories.TimeslotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TimeslotService {
  private final TimeslotRepository repository;

  @Autowired
  public TimeslotService(TimeslotRepository repository) {
    this.repository = repository;
  }

  public Page<Timeslot> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Timeslot getById(long id) {
    var timeslot = this.repository.findById(id);

    return timeslot.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Timeslot create(Timeslot timeslot) {
    if (this.repository.findById(timeslot.getId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return this.repository.save(timeslot);
  }

  public Timeslot update(long id, Timeslot timeslot) {
    timeslot.setId(id);

    return this.repository.save(timeslot);
  }

  public void delete(long id) {
    var timeslot = this.repository.findById(id);
    if (timeslot.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(timeslot.get());
  }
}
