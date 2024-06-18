package de.uftos.repositories;

import de.uftos.entities.Room;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface RoomRepository extends ListPagingAndSortingRepository<Room, String>,
    ListCrudRepository<Room, String> {
}
