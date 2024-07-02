package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entity for a timetable.
 * Contains the id and the name of the timetable as well as the lessons that are a part of it.
 */
@Entity(name = "timetables")
@Data
@NoArgsConstructor
public class Timetable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  @JsonIgnore
  @OneToMany(mappedBy = "timetable")
  private List<Lesson> lessons;

  /**
   * Creates a new timetable.
   *
   * @param name the name of the timetable.
   */
  public Timetable(String name) {
    this.name = name;
  }
}
