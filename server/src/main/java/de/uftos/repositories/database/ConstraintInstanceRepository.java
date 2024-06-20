package de.uftos.repositories.database;

import de.uftos.entities.ConstraintInstance;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface ConstraintInstanceRepository
    extends ListPagingAndSortingRepository<ConstraintInstance, String>,
    ListCrudRepository<ConstraintInstance, String> {
}
