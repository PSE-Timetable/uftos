package de.uftos.repositories.database;

import de.uftos.entities.Lesson;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the lesson database table.
 */
public interface LessonRepository extends ListPagingAndSortingRepository<Lesson, String>,
    ListCrudRepository<Lesson, String>, JpaSpecificationExecutor<Lesson> {
}
