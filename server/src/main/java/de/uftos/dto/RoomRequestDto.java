package de.uftos.dto;

import de.uftos.entities.Room;
import java.util.List;

public record RoomRequestDto(String name, String buildingName, int capacity, List<String> tagIds) {
  public Room map() {
    return new Room(this.name, this.buildingName, this.capacity, this.tagIds);
  }
}
