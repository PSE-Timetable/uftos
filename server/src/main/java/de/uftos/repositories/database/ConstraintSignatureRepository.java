package de.uftos.repositories.database;

import de.uftos.entities.ConstraintSignature;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface ConstraintSignatureRepository
    extends ListPagingAndSortingRepository<ConstraintSignature, String>,
    ListCrudRepository<ConstraintSignature, String> {
}
