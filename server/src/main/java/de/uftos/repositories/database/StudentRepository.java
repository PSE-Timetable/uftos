package de.uftos.repositories.database;

import de.uftos.entities.Student;
import de.uftos.entities.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the student database table.
 */
public interface StudentRepository
    extends ListPagingAndSortingRepository<Student, String>, ListCrudRepository<Student, String>,
    JpaSpecificationExecutor<Student> {

  List<Student> findByTags(Tag tag);
}
