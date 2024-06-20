package de.uftos.repositories.database;

import de.uftos.entities.Curriculum;
import org.springframework.data.repository.ListCrudRepository;

/**
 * The repository for accessing the curriculum database table.
 */
public interface CurriculumRepository extends ListCrudRepository<Curriculum, String> {
}
