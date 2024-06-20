package de.uftos.controller;

import de.uftos.dto.SubjectRequestDto;
import de.uftos.entities.Subject;
import de.uftos.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for the subject entity.
 * This controller handles /subjects HTTP requests.
 */
@RestController
@RequestMapping("/subjects")
public class SubjectController {
  private final SubjectService subjectService;

  /**
   * Creates a subject controller.
   *
   * @param subjectService the service for the subject entity.
   */
  @Autowired
  public SubjectController(SubjectService subjectService) {
    this.subjectService = subjectService;
  }

  /**
   * Maps the HTTP POST request, to create a new subject in the database, to the
   * {@link SubjectService#create(SubjectRequestDto) create} function of the subject service.
   *
   * @param subject the subject which is to be created.
   * @return the created subject with the assigned ID.
   */
  @PostMapping()
  public Subject createSubject(@RequestBody SubjectRequestDto subject) {
    return this.subjectService.create(subject);
  }

  /**
   * Maps the HTTP GET request for a set of subjects from the database to the
   * {@link SubjectService#get(Pageable) get} function of the subject service.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of subjects fitting the parameters.
   */
  @GetMapping()
  public Page<Subject> getSubjects(Pageable pageable) {
    return this.subjectService.get(pageable);
  }

  /**
   * Maps the HTTP GET request for a subject with the given ID to the
   * {@link SubjectService#getById(String) getById} function of the subject service.
   *
   * @param id the ID of the subject.
   * @return the subject with the given ID.
   */
  @GetMapping("/{id}")
  public Subject getSubject(@PathVariable String id) {
    return this.subjectService.getById(id);
  }

  /**
   * Maps the HTTP PUT request to update a subject to the
   * {@link SubjectService#update(String, SubjectRequestDto) update} function of
   * the subject service.
   *
   * @param id      the ID of the subject which is to be updated.
   * @param subject the updated information of the subject.
   * @return the updated subject.
   */
  @PutMapping("/{id}")
  public Subject updateSubject(@PathVariable String id, @RequestBody SubjectRequestDto subject) {
    return this.subjectService.update(id, subject);
  }

  /**
   * Maps the HTTP DELETE request to the {@link SubjectService#delete(String) delete} function
   * of the subject service.
   *
   * @param id the ID of the subject which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteSubject(@PathVariable String id) {
    this.subjectService.delete(id);
  }
}
