package de.uftos.repositories;

import de.uftos.entities.Teacher;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface TeacherRepository extends ListPagingAndSortingRepository<Teacher, Long>,
    ListCrudRepository<Teacher, Long> {
}
