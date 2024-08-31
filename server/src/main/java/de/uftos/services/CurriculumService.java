package de.uftos.services;


import de.uftos.dto.requestdtos.CurriculumRequestDto;
import de.uftos.dto.responsedtos.CurriculumResponseDto;
import de.uftos.dto.responsedtos.GradeResponseDto;
import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /curriculum endpoint.
 */
@Service
public class CurriculumService {
  private final CurriculumRepository repository;
  private final GradeRepository gradeRepository;

  /**
   * Creates a curriculum service.
   *
   * @param repository the repository for accessing the curriculum table.
   */
  @Autowired
  public CurriculumService(CurriculumRepository repository, GradeRepository gradeRepository) {
    this.repository = repository;
    this.gradeRepository = gradeRepository;
  }

  /**
   * Gets a page of entries of the curriculum table.
   *
   * @param name     the name filter
   * @param pageable contains the parameters for the page.
   * @return the page of the entries fitting the parameters.
   */
  public Page<CurriculumResponseDto> get(Pageable pageable, Optional<String> name) {
    //currently no filter for grades
    Specification<Curriculum> spec = new SpecificationBuilder<Curriculum>()
        .optionalOrLike(name, "name").build();
    List<CurriculumResponseDto> curricula = this.repository.findAll(spec, pageable).stream()
        .map(CurriculumResponseDto::new).toList();

    return new PageImpl<>(curricula);
  }

  /**
   * Gets a curriculum from their ID.
   *
   * @param id the ID of the curriculum.
   * @return the curriculum with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding curriculum.
   */
  public CurriculumResponseDto getById(String id) {
    Curriculum curriculum = this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Could not find a curriculum with this id"));
    return new CurriculumResponseDto(curriculum);
  }

  /**
   * Creates a new curriculum in the database.
   *
   * @param curriculum the information about the curriculum which is to be created.
   * @return the created curriculum which includes the ID that has been assigned.
   * @throws ResponseStatusException is thrown if the name of the curriculum is blank.
   */
  public CurriculumResponseDto create(CurriculumRequestDto curriculum) {
    if (curriculum.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The name of the curriculum is blank.");
    }
    Grade grade = gradeRepository.findById(curriculum.gradeId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Could not find a grade with this id"));
    return this.mapResponseDto(this.repository.save(curriculum.map(grade)));
  }

  private CurriculumResponseDto mapResponseDto(Curriculum curriculum) {
    return new CurriculumResponseDto(curriculum.getId(), curriculum.getName(),
        GradeResponseDto.createResponseDtoFromGrade(curriculum.getGrade()),
        curriculum.getLessonsCounts());
  }

  /**
   * Updates the curriculum with the given ID.
   *
   * @param id                the ID of the curriculum which is to be updated.
   * @param curriculumRequest the updated curriculum information
   * @return the updated grade.
   */
  public CurriculumResponseDto update(String id, CurriculumRequestDto curriculumRequest) {
    Grade grade = gradeRepository.findById(curriculumRequest.gradeId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Could not find a grade with this id"));
    Curriculum curriculum = curriculumRequest.map(grade);
    this.repository.delete(this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Could not find a curriculum with this id")));
    return new CurriculumResponseDto(this.repository.save(curriculum));
  }

  /**
   * Deletes the curriculum with the given ID.
   *
   * @param id the ID of the curriculum which is to be deleted.
   * @throws ResponseStatusException is thrown if no curriculum exists with the given ID.
   */
  public void delete(String id) {
    Optional<Curriculum> curriculum = this.repository.findById(id);
    if (curriculum.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Could not find a curriculum with this id");
    }

    this.repository.delete(curriculum.get());
  }
}

