package de.uftos.services;

import de.uftos.dto.LessonResponseDto;
import de.uftos.dto.RoomRequestDto;
import de.uftos.entities.Room;
import de.uftos.repositories.RoomRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoomService {
  private final RoomRepository repository;

  @Autowired
  public RoomService(RoomRepository repository) {
    this.repository = repository;
  }

  public Page<Room> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Room getById(String id) {
    Optional<Room> room = this.repository.findById(id);

    return room.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public List<LessonResponseDto> getLessonsById(String id) {
    Room room = this.getById(id);
    // TODO
    return null;
  }

  public Room create(RoomRequestDto room) {
    return this.repository.save(room.map());
  }

  public Room update(String id, RoomRequestDto roomRequest) {
    Room room = roomRequest.map();
    room.setId(id);

    return this.repository.save(room);
  }

  public void delete(String id) {
    var room = this.repository.findById(id);
    if (room.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(room.get());
  }
}
