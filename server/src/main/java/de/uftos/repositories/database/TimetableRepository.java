package de.uftos.repositories.database;

import de.uftos.entities.Timetable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the timetable database table.
 */
public interface TimetableRepository extends ListPagingAndSortingRepository<Timetable, String>,
    ListCrudRepository<Timetable, String> {
}
