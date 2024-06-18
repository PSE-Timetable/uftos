package de.uftos.repositories.entities;

import de.uftos.entities.Student;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface StudentRepository
    extends ListPagingAndSortingRepository<Student, String>, ListCrudRepository<Student, String> {
}
