package de.uftos.services;


import de.uftos.dto.CurriculumRequestDto;
import de.uftos.dto.CurriculumResponseDto;
import de.uftos.repositories.database.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /curriculum endpoint.
 */
@Service
public class CurriculumService {
  private final CurriculumRepository repository;

  /**
   * Creates a curriculum service.
   *
   * @param repository the repository for accessing the curriculum table.
   */
  @Autowired
  public CurriculumService(CurriculumRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the curriculum table.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of the entries fitting the parameters.
   */
  public Page<CurriculumResponseDto> get(Pageable pageable) {
    return null;
  }

  /**
   * Gets a curriculum from their ID.
   *
   * @param id the ID of the curriculum.
   * @return the curriculum with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding curriculum.
   */
  public CurriculumResponseDto getById(String id) {
    return null;
  }

  /**
   * Creates a new curriculum in the database.
   *
   * @param curriculum the information about the curriculum which is to be created.
   * @return the created curriculum which includes the ID that has been assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the curriculum parameter
   *                                 is already present in the database.
   */
  public CurriculumResponseDto create(CurriculumRequestDto curriculum) {
    return null;
  }

  /**
   * Updates the curriculum with the given ID.
   *
   * @param id                the ID of the curriculum which is to be updated.
   * @param curriculumRequest the updated curriculum information
   * @return the updated grade.
   */
  public CurriculumResponseDto update(String id, CurriculumRequestDto curriculumRequest) {
    return null;
  }

  /**
   * Deletes the curriculum with the given ID.
   *
   * @param id the ID of the curriculum which is to be deleted.
   * @throws ResponseStatusException is thrown if no curriculum exists with the given ID.
   */
  public void delete(String id) {
    var curriculum = this.repository.findById(id);
    if (curriculum.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(curriculum.get());
  }
}

