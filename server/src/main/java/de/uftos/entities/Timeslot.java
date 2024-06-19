package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uftos.dto.Weekday;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private String id;

  @Enumerated(EnumType.STRING)
  private Weekday day;
  private int slot;

  @ManyToMany
  private List<Tag> tags;

  @OneToMany
  @JsonIgnore
  private List<Lesson> lessons;

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
}

