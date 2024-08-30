package de.uftos.controller;

import de.uftos.dto.requestdtos.RoomRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Room;
import de.uftos.services.RoomService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for the room entity.
 * This controller handles /rooms HTTP requests.
 */
@RestController
@RequestMapping("/rooms")
public class RoomController {
  private final RoomService roomService;

  /**
   * Creates a room controller.
   *
   * @param roomService the service for the room entity.
   */
  @Autowired
  public RoomController(RoomService roomService) {
    this.roomService = roomService;
  }

  /**
   * Maps the HTTP POST request, to create a new room in the database, to the
   * {@link RoomService#create(RoomRequestDto) create} function of the room service.
   *
   * @param room the room which is to be created.
   * @return the created room with the assigned ID.
   */
  @PostMapping()
  public Room createRoom(@RequestBody RoomRequestDto room) {
    return this.roomService.create(room);
  }

  /**
   * Maps the HTTP GET request for a set of rooms from the database to the
   * {@link RoomService#get(Pageable, Optional, Optional, Optional)  get} function of
   * the room service.
   *
   * @param pageable contains the parameters for the page.
   * @param search   the search filter.
   * @param capacity the capacity filter.
   * @param tags     the tags filter.
   * @return the page of rooms fitting the parameters.
   */
  @GetMapping()
  public Page<Room> getRooms(Pageable pageable, Optional<String> search, Optional<Integer> capacity,
                             Optional<String[]> tags) {
    return this.roomService.get(pageable, search, capacity, tags);
  }

  /**
   * Maps the HTTP GET request for a room with the given ID to the
   * {@link RoomService#getById(String) getById} function of the room service.
   *
   * @param id the ID of the room.
   * @return the room with the given ID.
   */
  @GetMapping("/{id}")
  public Room getRoom(@PathVariable String id) {
    return this.roomService.getById(id);
  }

  /**
   * Maps the HTTP GET request, to get the lessons that get taught in the room, to the
   * {@link RoomService#getLessonsById(String) getLessonsById} function of the room service.
   *
   * @param id the ID of the room.
   * @return information about the lessons that get taught in the room.
   */
  @GetMapping("/{id}/lessons")
  public LessonResponseDto getRoomLessons(@PathVariable String id) {
    return this.roomService.getLessonsById(id);
  }

  /**
   * Maps the HTTP PUT request to update a room to the
   * {@link RoomService#update(String, RoomRequestDto) update} function of the room service.
   *
   * @param id   the ID of the room which is to be updated.
   * @param room the updated information of the room.
   * @return the updated room.
   */
  @PutMapping("/{id}")
  public Room updateRoom(@PathVariable String id, @RequestBody RoomRequestDto room) {
    return this.roomService.update(id, room);
  }

  /**
   * Maps the HTTP DELETE request to the {@link RoomService#deleteRooms(String[]) delete} function of the
   * room service.
   *
   * @param ids the IsD of the rooms which are to be deleted.
   */
  @DeleteMapping()
  public void deleteRooms(@RequestBody String[] ids) {
    this.roomService.deleteRooms(ids);
  }
}
