package de.uftos.services;

import de.uftos.dto.SubjectRequestDto;
import de.uftos.entities.Subject;
import de.uftos.repositories.SubjectRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SubjectService {
  private final SubjectRepository repository;

  @Autowired
  public SubjectService(SubjectRepository repository) {
    this.repository = repository;
  }

  public Page<Subject> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Subject getById(String id) {
    Optional<Subject> subject = this.repository.findById(id);

    return subject.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Subject create(SubjectRequestDto subject) {
    return this.repository.save(subject.map());
  }

  public Subject update(String id, SubjectRequestDto subjectRequest) {
    Subject subject = subjectRequest.map();
    subject.setId(id);

    return this.repository.save(subject);
  }

  public void delete(String id) {
    var subject = this.repository.findById(id);
    if (subject.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(subject.get());
  }
}
