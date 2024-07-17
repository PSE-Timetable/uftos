package de.uftos.controller;

import de.uftos.dto.ConstraintInstanceRequestDto;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.services.ConstraintInstanceService;
import de.uftos.services.ConstraintSignatureService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for the constraint signature and constraint instance entity.
 * This controller handles /constraints HTTP requests.
 */
@RestController
@RequestMapping("/constraints")
public class ConstraintController {
  private final ConstraintSignatureService constraintSignatureService;
  private final ConstraintInstanceService constraintInstanceService;
  private final ConstraintInstanceRepository constraintInstanceRepository;
  private final ConstraintSignatureRepository constraintSignatureRepository;

  /**
   * Creates the constraint controller.
   *
   * @param constraintSignatureService the service for the constraint signature entity.
   * @param constraintInstanceService  the service for the constraint instance entity.
   */
  @Autowired
  public ConstraintController(ConstraintSignatureService constraintSignatureService,
                              ConstraintInstanceService constraintInstanceService,
                              ConstraintInstanceRepository constraintInstanceRepository,
                              ConstraintSignatureRepository constraintSignatureRepository) {
    this.constraintSignatureService = constraintSignatureService;
    this.constraintInstanceService = constraintInstanceService;
    this.constraintInstanceRepository = constraintInstanceRepository;
    this.constraintSignatureRepository = constraintSignatureRepository;
  }

  /**
   * Maps the HTTP GET request for a set of constraints signatures from the database, to the
   * {@link ConstraintSignatureService#get(Pageable, Optional) get} function of the
   * constraint signature service.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @return the page of constraint signatures fitting the parameters.
   */
  @GetMapping()
  public Page<ConstraintSignature> getConstraintSignatures(Pageable pageable,
                                                           Optional<String> name) {
    return this.constraintSignatureService.get(pageable, name);
  }

  /**
   * Maps the HTTP GET request for a constraint signature with the given ID to the
   * {@link ConstraintSignatureService#getById(String)}  getById} function of the
   * constraint signature service.
   *
   * @param signatureId the ID of the student.
   * @return the constraint signature with the given ID.
   */
  @GetMapping("/{signatureId}")
  public ConstraintSignature getConstraintSignature(@PathVariable String signatureId) {
    return this.constraintSignatureService.getById(signatureId);
  }

  /**
   * Maps the HTTP POST request, to create a new constraint instance in the database, to the
   * {@link ConstraintInstanceService#create(String, ConstraintInstanceRequestDto) create} function
   * of the constraint instance service.
   *
   * @param signatureId the constraint signature for which an instance is to be created.
   * @param request     the constraint instance values.
   * @return the created student with the assigned ID.
   */
  @PostMapping("/{signatureId}/instances")
  public ConstraintInstance createConstraintInstance(@PathVariable String signatureId,
                                                     ConstraintInstanceRequestDto request) {
    return this.constraintInstanceService.create(signatureId, request);
  }

  /**
   * Maps the HTTP GET request for a constraint signature with the given ID to the
   * {@link ConstraintSignatureService#getById(String)}  getById} function of
   * the constraint signature service.
   *
   * @param signatureId the constraint signature for which an instance is to be created.
   * @param pageable    contains the parameters for the page.
   * @param argument    the argument filter.
   * @return the constraint signature with the given ID.
   */
  @GetMapping("/{signatureId}/instances")
  public Page<ConstraintInstance> getConstraintInstances(@PathVariable String signatureId,
                                                         Pageable pageable,
                                                         Optional<String> argument) {
    return this.constraintInstanceService.get(signatureId, pageable, argument);
  }

  /**
   * Maps the HTTP GET request for a constraint instance with the given ID to the
   * {@link ConstraintInstanceService#getById(String, String)  getById} function of
   * the constraint instance service.
   *
   * @param signatureId the constraint signature for which an instance is to be created.
   * @param id          the id of the instance to return.
   * @return the constraint instance with the given ID.
   */
  @GetMapping("/{signatureId}/instances/{id}")
  public ConstraintInstance getConstraintInstanceById(@PathVariable String signatureId,
                                                      @PathVariable String id) {
    return this.constraintInstanceService.getById(signatureId, id);
  }

  /**
   * Maps the HTTP PUT request to update a student to the
   * {@link ConstraintInstanceService#update(String, String, ConstraintInstanceRequestDto) update}
   * function of the student service.
   *
   * @param signatureId the constraint signature for which an instance is to be created.
   * @param id          the id of the instance to return.
   * @param request     the updated information of the constraint instance.
   * @return the updated constraint instance.
   */
  @PutMapping("/{signatureId}/instances/{id}")
  public ConstraintInstance updateConstraintInstanceById(@PathVariable String signatureId,
                                                         @PathVariable String id,
                                                         ConstraintInstanceRequestDto request) {
    return this.constraintInstanceService.update(signatureId, id, request);
  }

  /**
   * Maps the HTTP DELETE request to the
   * {@link ConstraintInstanceService#delete(String, String) delete} function of
   * the student service.
   *
   * @param id the ID of the student which is to be deleted.
   */
  @DeleteMapping("/{signatureId}/instances/{id}")
  public void deleteConstraintInstance(@PathVariable String signatureId,
                                       @PathVariable String id
  ) {
    this.constraintInstanceService.delete(signatureId, id);
  }

}
