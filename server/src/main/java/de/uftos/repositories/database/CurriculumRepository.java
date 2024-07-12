package de.uftos.repositories.database;

import de.uftos.entities.Curriculum;
import de.uftos.entities.Teacher;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the curriculum database table.
 */
public interface CurriculumRepository extends ListPagingAndSortingRepository<Curriculum, String>,
    ListCrudRepository<Curriculum, String> {
}
