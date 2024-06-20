package de.uftos.services;

import de.uftos.dto.SubjectRequestDto;
import de.uftos.entities.Subject;
import de.uftos.repositories.database.SubjectRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /subjects endpoint.
 */
@Service
public class SubjectService {
  private final SubjectRepository repository;

  /**
   * Creates a subject service.
   *
   * @param repository The repository for accessing the subject table.
   */
  @Autowired
  public SubjectService(SubjectRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the subject table.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of the entries fitting the parameters.
   */
  public Page<Subject> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  /**
   * Gets a subject from their ID.
   *
   * @param id the ID of the subject.
   * @return The subject with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding subject.
   */
  public Subject getById(String id) {
    Optional<Subject> subject = this.repository.findById(id);

    return subject.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new subject in the database.
   *
   * @param subject the subject which to be created.
   * @return The updated subject which includes the ID that has been assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the subject parameter is
   *                                 already present in the database.
   */
  public Subject create(SubjectRequestDto subject) {
    return this.repository.save(subject.map());
  }

  /**
   * Updates the subject with the given ID.
   *
   * @param id             the ID of the subject which is to be updated.
   * @param subjectRequest the updated subject information.
   * @return the updated subject.
   */
  public Subject update(String id, SubjectRequestDto subjectRequest) {
    Subject subject = subjectRequest.map();
    subject.setId(id);

    return this.repository.save(subject);
  }

  /**
   * Deletes the subject with the given ID.
   *
   * @param id the ID of the subject. which to be deleted.
   * @throws ResponseStatusException is thrown if no subject exists with the given ID.
   */
  public void delete(String id) {
    var subject = this.repository.findById(id);
    if (subject.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(subject.get());
  }
}
