package de.uftos.repositories.database;

import de.uftos.entities.Lesson;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the lesson database entity.
 */
public interface LessonRepository extends ListPagingAndSortingRepository<Lesson, String>,
    ListCrudRepository<Lesson, String> {
}
