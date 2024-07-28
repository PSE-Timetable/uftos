package de.uftos.repositories.database;

import de.uftos.entities.StudentGroup;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;


/**
 * The repository for accessing the student group database table.
 */
public interface StudentGroupRepository
    extends ListPagingAndSortingRepository<StudentGroup, String>,
    ListCrudRepository<StudentGroup, String>, JpaSpecificationExecutor<StudentGroup> {
}