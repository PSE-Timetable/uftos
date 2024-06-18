package de.uftos.repositories;

import de.uftos.entities.Timetable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface TimetableRepository extends ListPagingAndSortingRepository<Timetable, String>,
    ListCrudRepository<Timetable, String> {
}
