package de.uftos.repositories.entities;

import de.uftos.entities.Teacher;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface TeacherRepository extends ListPagingAndSortingRepository<Teacher, String>,
    ListCrudRepository<Teacher, String> {
}
