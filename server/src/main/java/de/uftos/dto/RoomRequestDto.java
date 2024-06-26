package de.uftos.dto;

import de.uftos.entities.Room;
import java.util.List;

/**
 * A data transfer object used in the room HTTP requests.
 *
 * @param name         the name of the room.
 * @param buildingName the name of the building in which the room is located.
 * @param capacity     the capacity of the room.
 * @param tagIds       the IDs of the tags associated with the room.
 */
public record RoomRequestDto(String name, String buildingName, int capacity, List<String> tagIds) {

  /**
   * Maps the information from the data transfer object to a new room entity.
   *
   * @return the new room entity.
   */
  public Room map() {
    return new Room(this.name, this.buildingName, this.capacity, this.tagIds);
  }
}
