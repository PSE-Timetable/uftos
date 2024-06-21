package de.uftos.services;

import de.uftos.entities.ConstraintSignature;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service for accessing the constraintSignature database entity.
 */
@Service
public class ConstraintSignatureService {
  private final ConstraintSignatureRepository repository;

  /**
   * Creates a constraintSignature service.
   *
   * @param repository the repository for accessing the constraintSignature entity.
   */
  @Autowired
  public ConstraintSignatureService(ConstraintSignatureRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the constraintSignature entity.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @return the page of the entries fitting the parameters.
   */
  public Page<ConstraintSignature> get(Pageable pageable, Optional<String> name) {
    return this.repository.findAll(pageable);
  }

  /**
   * Gets a constraintSignature from their ID.
   *
   * @param id the ID of the constraintSignature.
   * @return the constraintSignature with the given ID.
   * @throws ResponseStatusException if the ID doesn't have a corresponding constraintSignature.
   */
  public ConstraintSignature getById(String id) {
    return this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }
}
