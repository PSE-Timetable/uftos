package de.uftos.repositories.database;

import de.uftos.entities.Room;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the room database entity.
 */
public interface RoomRepository extends ListPagingAndSortingRepository<Room, String>,
    ListCrudRepository<Room, String> {
}
