package de.uftos.repositories.database;

import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * The repository for accessing the constraint signature database table.
 */
public interface ConstraintSignatureRepository
    extends ListPagingAndSortingRepository<ConstraintSignature, String>,
    ListCrudRepository<ConstraintSignature, String>, JpaSpecificationExecutor<ConstraintSignature> {

  @Query("SELECT ci FROM constraint_signature cs JOIN cs.instances ci WHERE cs.name = :signatureId")
  Page<ConstraintInstance> findInstancesBySignatureId(@Param("signatureId") String signatureId, Pageable pageable);

}
