package de.uftos.services;

import de.uftos.dto.requestdtos.SubjectRequestDto;
import de.uftos.entities.Curriculum;
import de.uftos.entities.Subject;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /subjects endpoint.
 */
@Service
public class SubjectService {
  private final SubjectRepository repository;
  private final CurriculumRepository curriculumRepository;

  /**
   * Creates a subject service.
   *
   * @param repository The repository for accessing the subject table.
   */
  @Autowired
  public SubjectService(SubjectRepository repository, CurriculumRepository curriculumRepository) {
    this.repository = repository;
    this.curriculumRepository = curriculumRepository;
  }

  /**
   * Gets a page of entries of the subject table.
   *
   * @param sort   contains the sort parameters.
   * @param search the search filter.
   * @return the page of the entries fitting the parameters.
   */
  public List<Subject> get(Sort sort, Optional<String> search) {
    Specification<Subject> specification = new SpecificationBuilder<Subject>()
        .search(search)
        .build();

    return this.repository.findAll(specification, sort);
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
   * @throws ResponseStatusException is thrown if the name of the subject is blank.
   */
  public Subject create(SubjectRequestDto subject) {
    if (subject.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    return this.repository.save(subject.map());
  }

  /**
   * Updates the subject with the given ID.
   *
   * @param id             the ID of the subject which is to be updated.
   * @param subjectRequest the updated subject information.
   * @return the updated subject.
   * @throws ResponseStatusException is thrown if the name of the subject is blank.
   */
  public Subject update(String id, SubjectRequestDto subjectRequest) {
    if (subjectRequest.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The name of the subject is blank.");
    }
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
    Optional<Subject> subjectOptional = this.repository.findById(id);
    if (subjectOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The name of the subject is blank.");
    }

    Subject subject = subjectOptional.get();

    List<Curriculum> curriculums = curriculumRepository.findAll();

    for (Curriculum curriculum : curriculums) {
      curriculum.getLessonsCounts()
          .removeIf((lessonsCount) -> lessonsCount.getSubject().equals(subject));
    }

    curriculumRepository.saveAll(curriculums);
    this.repository.delete(subject);
  }
}
