package de.uftos.services;

import de.uftos.dto.LessonResponseDto;
import de.uftos.dto.RoomRequestDto;
import de.uftos.entities.Room;
import de.uftos.repositories.database.RoomRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service for accessing the room database entity.
 */
@Service
public class RoomService {
  private final RoomRepository repository;

  /**
   * Creates a lesson service.
   *
   * @param repository the repository for accessing the lesson entity.
   */
  @Autowired
  public RoomService(RoomRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the lesson entity.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of entries fitting the parameters.
   */
  public Page<Room> get(Pageable pageable, Optional<String> name,
                        Optional<String> buildingName, Optional<Integer> capacity,
                        Optional<String[]> tags) {
    return this.repository.findAll(pageable);
  }

  /**
   * Gets a room from their ID.
   *
   * @param id the ID of the room.
   * @return the room with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding room.
   */
  public Room getById(String id) {
    Optional<Room> room = this.repository.findById(id);

    return room.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Gets the information about the lessons that happen in the room.
   *
   * @param id the ID of the room.
   * @return a list of objects each containing information about a lesson.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding room.
   */
  public List<LessonResponseDto> getLessonsById(String id) {
    Room room = this.getById(id);
    // TODO
    return null;
  }

  /**
   * Creates a new room in the database.
   *
   * @param room the information about the room which is to be created.
   * @return the created room which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the room parameter is
   *                                 already present in the database.
   */
  public Room create(RoomRequestDto room) {
    return this.repository.save(room.map());
  }

  /**
   * Updates the room with the given ID.
   *
   * @param id          the ID of the room which is to be updated.
   * @param roomRequest the updated room information.
   * @return the updated room.
   */
  public Room update(String id, RoomRequestDto roomRequest) {
    Room room = roomRequest.map();
    room.setId(id);

    return this.repository.save(room);
  }

  /**
   * Deletes the room with the given ID.
   *
   * @param id the ID of the room which is to be deleted.
   * @throws ResponseStatusException is thrown if no room exists with the given ID.
   */
  public void delete(String id) {
    var room = this.repository.findById(id);
    if (room.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(room.get());
  }
}
