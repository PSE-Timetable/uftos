package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uftos.dto.Weekday;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The database entity for timeslots.
 * Contains an ID, the day and slot for each day, as well as the tags and lessons associated
 * with the timeslot.
 */
@Entity(name = "timeslots")
@Data
@NoArgsConstructor
public class Timeslot {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @NotEmpty
  private String id;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Weekday day;

  @PositiveOrZero
  @NotNull
  private int slot;

  @NotNull
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "timeslots_tags",
      joinColumns = @JoinColumn(name = "timeslots_id"),
      inverseJoinColumns = @JoinColumn(name = "tags_id"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"timeslots_id", "tags_id"}))
  private List<Tag> tags = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "timeslot")
  private List<Lesson> lessons = new ArrayList<>();

  /**
   * Creates a new timeslot.
   *
   * @param day    the day of the timeslot.
   * @param slot   the slot in the day.
   * @param tagIds the IDs of the tags associated with the timeslot.
   */
  public Timeslot(Weekday day, int slot, List<String> tagIds) {
    this.day = day;
    this.slot = slot;
    this.tags = tagIds.stream().map(Tag::new).toList();
  }

  /**
   * Creates a new timeslot.
   * Used if the ID is known.
   *
   * @param id the ID of the timeslot.
   */
  public Timeslot(String id) {
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
    Timeslot that = (Timeslot) other;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int initialOddNumber = 41;
    int multiplierOddNumber = 131;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(slot)
        .append(tags)
        .toHashCode();
  }
}
