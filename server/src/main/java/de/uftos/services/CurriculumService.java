package de.uftos.services;


import de.uftos.dto.CurriculumRequestDto;
import de.uftos.dto.CurriculumResponseDto;
import de.uftos.entities.Curriculum;
import de.uftos.repositories.database.CurriculumRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

  public List<CurriculumResponseDto> get() {
    return this.repository.findAll().stream().map(
        curriculum -> new CurriculumResponseDto(curriculum.getId(),
            curriculum.getGrade().getName())).toList();
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

