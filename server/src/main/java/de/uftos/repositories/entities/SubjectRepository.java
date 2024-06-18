package de.uftos.repositories.entities;

import de.uftos.entities.Subject;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface SubjectRepository
    extends ListPagingAndSortingRepository<Subject, String>, ListCrudRepository<Subject, String> {
}
