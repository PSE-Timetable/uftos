package de.uftos.services;

import de.uftos.dto.ConstraintInstanceRequestDto;
import de.uftos.entities.ConstraintInstance;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service for accessing the constraintInstance database entity.
 */
@Service
public class ConstraintInstanceService {
  private final ConstraintInstanceRepository repository;

  /**
   * Creates a constraintInstance service.
   *
   * @param repository the repository for accessing the constraintInstance entity.
   */
  @Autowired
  public ConstraintInstanceService(ConstraintInstanceRepository repository) {
    this.repository = repository;
  }

  /**
   * Creates a new constraint instance
   *
   * @param signatureId the constraint signature id
   * @param request     the constraint instance request object
   * @return the newly created constraint instance
   */
  public ConstraintInstance create(String signatureId, ConstraintInstanceRequestDto request) {
    return null;
  }

  /**
   * Gets a page of entries of the constraintInstance entity.
   *
   * @param signatureId the ID of the signature for the instances.
   * @param pageable    contains the parameters for the page.
   * @return the page of the entries fitting the parameters.
   */
  public Page<ConstraintInstance> get(String signatureId, Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  /**
   * Gets a constraintInstance by their ID.
   *
   * @param id the ID of the constraintInstance.
   * @return the constraintInstance with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding constraintInstance.
   */
  public ConstraintInstance getById(String signatureId, String id) {
    return this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Updates a constraintInstance by their ID.
   *
   * @param signatureId the ID of the constraint signature.
   * @param id          the ID of the constraintInstance.
   * @param request     the new constraint instance to which the existing instance should be updated
   */
  public ConstraintInstance update(String signatureId, String id,
                                   ConstraintInstanceRequestDto request) {
    return null;
  }

  /**
   * Deletes a constraintInstance by their ID.
   *
   * @param signatureId the ID of the constraint signature.
   * @param id          the ID of the constraintInstance.
   */
  public void delete(String signatureId, String id) {

  }
}
