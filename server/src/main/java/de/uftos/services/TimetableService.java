package de.uftos.services;

import de.uftos.entities.Timetable;
import de.uftos.repositories.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TimetableService {
  private final TimetableRepository repository;

  @Autowired
  public TimetableService(TimetableRepository repository) {
    this.repository = repository;
  }

  public Page<Timetable> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Timetable getById(long id) {
    var timetable = this.repository.findById(id);

    return timetable.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Timetable create(Timetable timetable) {
    // solving magic here
    return null;
  }

  public void delete(long id) {
    var timetable = this.repository.findById(id);
    if (timetable.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(timetable.get());
  }
}
