package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
  @NotEmpty
  private String id;

  @NotEmpty
  private String name;

  @JsonIgnore
  @OneToMany(mappedBy = "timetable")
  private List<Lesson> lessons = new ArrayList<>();

  /**
   * Creates a new timetable.
   *
   * @param name the name of the timetable.
   */
  public Timetable(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Timetable that = (Timetable) other;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int initialOddNumber = 59;
    int multiplierOddNumber = 113;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(name)
        .toHashCode();
  }
}
