package de.uftos.repositories.database;

import de.uftos.entities.Timetable;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the timetable database table.
 */
public interface TimetableRepository extends ListPagingAndSortingRepository<Timetable, String>,
    ListCrudRepository<Timetable, String> {

  @Query("SELECT t FROM timetables t LEFT JOIN t.lessons l WHERE l.id IN :lessonIds")
  List<Timetable> findAllByLessons(List<String> lessonIds);
}
