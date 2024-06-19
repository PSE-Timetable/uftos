package de.uftos.repositories;

import de.uftos.entities.Grade;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the grade database entity.
 */
public interface GradeRepository extends ListPagingAndSortingRepository<Grade, String>,
    ListCrudRepository<Grade, String> {
}
