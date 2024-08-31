package de.uftos.repositories.database;

import de.uftos.entities.Room;
import de.uftos.entities.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the room database table.
 */
public interface RoomRepository extends ListPagingAndSortingRepository<Room, String>,
    ListCrudRepository<Room, String>, JpaSpecificationExecutor<Room> {

  List<Room> findByTags(Tag tag);

  @Query("SELECT r FROM rooms r JOIN r.tags t WHERE t IN :tagIds")
  List<Room> findAllByTags(List<String> tagIds);
}
