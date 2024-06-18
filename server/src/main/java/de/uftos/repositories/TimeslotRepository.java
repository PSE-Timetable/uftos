package de.uftos.repositories;

import de.uftos.entities.Timeslot;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface TimeslotRepository extends ListPagingAndSortingRepository<Timeslot, String>,
    ListCrudRepository<Timeslot, String> {
}
