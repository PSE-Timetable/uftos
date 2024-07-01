package de.uftos.repositories.database;

import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the constraint instance database table.
 */
public interface ConstraintInstanceRepository
    extends ListPagingAndSortingRepository<ConstraintInstance, String>,
    ListCrudRepository<ConstraintInstance, String>, JpaSpecificationExecutor<ConstraintInstance> {

  Optional<ConstraintInstance> findBySignatureAndId(ConstraintSignature signature, String id);
}
