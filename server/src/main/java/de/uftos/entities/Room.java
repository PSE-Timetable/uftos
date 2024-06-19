package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entity for rooms.
 * Contains an ID, the name as well as the building and capacity of the room and the tags and
 * lessons associated with it.
 */
@Entity(name = "rooms")
@Data
@NoArgsConstructor
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;
  private String buildingName;
  private int capacity;

  @ManyToMany
  private List<Tag> tags;

  @OneToMany
  @JsonIgnore
  private List<Lesson> lessons;

  /**
   * Creates a new room.
   *
   * @param name         the name of the room.
   * @param buildingName the name of the building in which room is located.
   * @param capacity     the capacity of the room.
   * @param tagIds       the IDs of the tags associated with the room.
   */
  public Room(String name, String buildingName, int capacity, List<String> tagIds) {
    this.name = name;
    this.buildingName = buildingName;
    this.capacity = capacity;
    this.tags = tagIds.stream().map(Tag::new).toList();
  }
}
