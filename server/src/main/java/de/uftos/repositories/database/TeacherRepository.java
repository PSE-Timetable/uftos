package de.uftos.repositories.database;

import de.uftos.entities.Teacher;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the teacher database table.
 */
public interface TeacherRepository extends ListPagingAndSortingRepository<Teacher, String>,
    ListCrudRepository<Teacher, String>, JpaSpecificationExecutor<Teacher> {
}
