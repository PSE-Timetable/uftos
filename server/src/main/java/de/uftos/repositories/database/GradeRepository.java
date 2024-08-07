package de.uftos.repositories.database;

import de.uftos.entities.Grade;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the grade database table.
 */
public interface GradeRepository extends ListPagingAndSortingRepository<Grade, String>,
    ListCrudRepository<Grade, String>, JpaSpecificationExecutor<Grade> {
}
