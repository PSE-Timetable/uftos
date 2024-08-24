package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.search.PostgreSQLTSVectorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

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
  @NotEmpty
  private String id;

  @NotEmpty
  private String name;
  @NotEmpty
  private String buildingName;
  @Positive
  @NotNull
  private int capacity;

  @NotNull
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "rooms_tags",
      joinColumns = @JoinColumn(name = "rooms_id"),
      inverseJoinColumns = @JoinColumn(name = "tags_id"))
  private List<Tag> tags;

  @NotNull
  @JsonIgnore
  @OneToMany(mappedBy = "room")
  private List<Lesson> lessons;

  @JsonIgnore
  @Type(PostgreSQLTSVectorType.class)
  @Column(name = "search_vector", columnDefinition = "tsvector", insertable = false, updatable = false)
  private String searchVector;

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

  public Room(String id) {
    this.id = id;
  }


  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Room that = (Room) other;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int initialOddNumber = 73;
    int multiplierOddNumber = 241;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(name)
        .append(buildingName)
        .append(capacity)
        .append(tags)
        .toHashCode();
  }
}
