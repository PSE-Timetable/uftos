package de.uftos.repositories;

import de.uftos.entities.Curriculum;
import org.springframework.data.repository.ListCrudRepository;

public interface CurriculumRepository extends ListCrudRepository<Curriculum, String> {
}
