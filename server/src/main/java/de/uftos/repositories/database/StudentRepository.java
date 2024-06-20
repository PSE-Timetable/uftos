package de.uftos.repositories.database;

import de.uftos.entities.Student;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the student database entity.
 */
public interface StudentRepository
    extends ListPagingAndSortingRepository<Student, String>, ListCrudRepository<Student, String> {
}
