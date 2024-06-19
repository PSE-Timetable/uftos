package de.uftos.repositories;

import de.uftos.entities.Subject;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the subject database entry.
 */
public interface SubjectRepository
    extends ListPagingAndSortingRepository<Subject, String>, ListCrudRepository<Subject, String> {
}
