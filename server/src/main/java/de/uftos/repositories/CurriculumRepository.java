package de.uftos.repositories;

import de.uftos.entities.Curriculum;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface CurriculumRepository
    extends ListPagingAndSortingRepository<Curriculum, String>, ListCrudRepository<Curriculum, String> {
}
