package de.uftos.services;

import de.uftos.entities.Grade;
import de.uftos.repositories.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GradeService {
  private final GradeRepository repository;

  @Autowired
  public GradeService(GradeRepository repository) {
    this.repository = repository;
  }

  public Page<Grade> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Grade getById(String id) {
    var grade = this.repository.findById(id);

    return grade.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Grade create(Grade grade) {
    if (this.repository.findById(grade.getId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return this.repository.save(grade);
  }

  public Grade update(String id, Grade grade) {
    grade.setId(id);

    return this.repository.save(grade);
  }

  public void delete(String id) {
    var grade = this.repository.findById(id);
    if (grade.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(grade.get());
  }
}
