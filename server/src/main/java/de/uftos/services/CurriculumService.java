package de.uftos.services;


import de.uftos.dto.CurriculumRequestDto;
import de.uftos.entities.Curriculum;
import de.uftos.repositories.CurriculumRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurriculumService {
  private final CurriculumRepository repository;

  @Autowired
  public CurriculumService(CurriculumRepository repository) {
    this.repository = repository;
  }

  public Page<Curriculum> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Curriculum getById(String id) {
    Optional<Curriculum> curriculum = this.repository.findById(id);

    return curriculum.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Curriculum create(CurriculumRequestDto curriculum) {
    return this.repository.save(curriculum.map());
  }

  public Curriculum update(String id, CurriculumRequestDto curriculumRequest) {
    Curriculum curriculum = curriculumRequest.map();
    curriculum.setId(id);

    return this.repository.save(curriculum);
  }

  public void delete(String id) {
    var curriculum = this.repository.findById(id);
    if (curriculum.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(curriculum.get());
  }
}

