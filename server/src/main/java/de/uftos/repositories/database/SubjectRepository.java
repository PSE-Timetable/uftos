package de.uftos.repositories.database;

import de.uftos.entities.Subject;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;

/**
 * The repository for accessing the subject database table.
 */
public interface SubjectRepository
    extends ListCrudRepository<Subject, String>, JpaSpecificationExecutor<Subject> {
}
