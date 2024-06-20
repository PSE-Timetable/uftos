package de.uftos.repositories.database;

import de.uftos.entities.Curriculum;
import org.springframework.data.repository.ListCrudRepository;

public interface CurriculumRepository extends ListCrudRepository<Curriculum, String> {
}
