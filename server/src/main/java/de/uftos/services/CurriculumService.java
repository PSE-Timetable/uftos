package de.uftos.services;


import de.uftos.dto.CurriculumRequestDto;
import de.uftos.dto.CurriculumResponseDto;
import de.uftos.repositories.database.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

  public Page<CurriculumResponseDto> get() {
    return null;
  }

  public CurriculumResponseDto getById(String id) {
    return null;
  }

  public CurriculumResponseDto create(CurriculumRequestDto curriculum) {
    return null;
  }

  public CurriculumResponseDto update(String id, CurriculumRequestDto curriculumRequest) {
    return null;
  }

  public void delete(String id) {
    var curriculum = this.repository.findById(id);
    if (curriculum.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(curriculum.get());
  }
}

