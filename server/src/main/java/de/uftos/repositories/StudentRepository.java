package de.uftos.repositories;

import de.uftos.entities.Student;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface StudentRepository extends ListPagingAndSortingRepository<Student, String>, ListCrudRepository<Student, String> {
}
