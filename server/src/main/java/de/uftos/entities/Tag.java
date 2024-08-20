package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The database entity for tags.
 * Contains an ID and the name of the tag. Additionally, the resources which have tags
 * associated with them are also saved.
 */
@Entity(name = "tags")
@Data
@NoArgsConstructor
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @NotEmpty
  private String id;

  @NotEmpty
  private String name;

  @JsonIgnore
  @ManyToMany(mappedBy = "tags")
  private List<Student> students;

  @JsonIgnore
  @ManyToMany(mappedBy = "tags")
  private List<Teacher> teachers;

  @JsonIgnore
  @ManyToMany(mappedBy = "tags")
  private List<StudentGroup> studentGroups;

  @JsonIgnore
  @ManyToMany(mappedBy = "tags")
  private List<Room> rooms;

  @JsonIgnore
  @ManyToMany(mappedBy = "tags")
  private List<Subject> subjects;

  @JsonIgnore
  @ManyToMany(mappedBy = "tags")
  private List<Grade> grades;

  @JsonIgnore
  @ManyToMany(mappedBy = "tags")
  private List<Timeslot> timeslots;

  /**
   * Creates a new tag.
   * Used if the ID is known.
   *
   * @param id the ID of the tag.
   */
  public Tag(String id) {
    this.id = id;
  }

  /**
   * Creates a new tag.
   *
   * @param id   unused.
   * @param name the name of the tag.
   */
  public Tag(String id, String name) {
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
    Tag that = (Tag) other;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int initialOddNumber = 101;
    int multiplierOddNumber = 211;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(name)
        .toHashCode();
  }

}
