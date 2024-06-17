package de.uftos.services;

import de.uftos.entities.Room;
import de.uftos.repositories.RoomRepository;
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

  public Room getById(long id) {
    var room = this.repository.findById(id);

    return room.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Room create(Room room) {
    if (this.repository.findById(room.getId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return this.repository.save(room);
  }

  public Room update(long id, Room room) {
    room.setId(id);

    return this.repository.save(room);
  }

  public void delete(long id) {
    var room = this.repository.findById(id);
    if (room.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(room.get());
  }
}
