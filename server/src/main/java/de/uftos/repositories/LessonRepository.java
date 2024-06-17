package de.uftos.repositories;

import de.uftos.entities.Lesson;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface LessonRepository extends ListPagingAndSortingRepository<Lesson, Long>,
    ListCrudRepository<Lesson, Long> {
}
