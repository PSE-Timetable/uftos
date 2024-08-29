package de.uftos.services;

import de.uftos.dto.requestdtos.RoomRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /rooms endpoint.
 */
@Service
public class RoomService {
  private final RoomRepository repository;
  private final ServerRepository serverRepository;

  /**
   * Creates a lesson service.
   *
   * @param repository the repository for accessing the lesson table.
   */
  @Autowired
  public RoomService(RoomRepository repository, ServerRepository serverRepository) {
    this.repository = repository;
    this.serverRepository = serverRepository;
  }

  /**
   * Gets a page of entries of the lesson table.
   *
   * @param pageable contains the parameters for the page.
   * @param search   the search filter.
   * @param capacity the capacity filter.
   * @param tags     the tags filter.
   * @return the page of entries fitting the parameters.
   */
  public Page<Room> get(Pageable pageable, Optional<String> search, Optional<Integer> capacity,
                        Optional<String[]> tags) { //currently no capacity filter
    Specification<Room> spec = new SpecificationBuilder<Room>()
        .search(search)
        .optionalAndJoinIn(tags, "tags", "id")
        .build();
    return this.repository.findAll(spec, pageable);
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

    return room.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Could not find a room with this id"));
  }

  /**
   * Gets the information about the lessons that happen in the room.
   *
   * @param id the ID of the room.
   * @return a list of objects each containing information about a lesson.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding room.
   */
  public LessonResponseDto getLessonsById(String id) {
    Room room = this.getById(id);
    List<Lesson> lessons = new ArrayList<>(room.getLessons());
    lessons.removeIf(lesson -> !lesson.getYear().equals(
        serverRepository.findAll().getFirst().getCurrentYear()));
    return LessonResponseDto.createResponseDtoFromLessons(lessons);
  }

  /**
   * Creates a new room in the database.
   *
   * @param room the information about the room which is to be created.
   * @return the created room which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the name, building name are blank or the capacity is 0.
   */
  public Room create(RoomRequestDto room) {
    if (room.name().isBlank() || room.buildingName().isBlank() || room.capacity() == 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The name, building name are blank or the capacity is 0.");
    }
    return this.repository.save(room.map());
  }

  /**
   * Updates the room with the given ID.
   *
   * @param id          the ID of the room which is to be updated.
   * @param roomRequest the updated room information.
   * @return the updated room.
   * @throws ResponseStatusException is thrown if the name, building name are blank or the capacity is 0.
   */
  public Room update(String id, RoomRequestDto roomRequest) {
    if (roomRequest.name().isBlank() || roomRequest.buildingName().isBlank() ||
        roomRequest.capacity() == 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The name, building name are blank or the capacity is 0.");
    }
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
    Optional<Room> room = this.repository.findById(id);
    if (room.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Could not find a room with this id");
    }

    this.repository.delete(room.get());
  }
}
