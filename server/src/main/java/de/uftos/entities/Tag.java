package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private String id;

  private String name;

  @ManyToMany
  @JsonIgnore
  private List<Student> students;

  @ManyToMany
  @JsonIgnore
  private List<Teacher> teachers;

  @ManyToMany
  @JsonIgnore
  private List<StudentGroup> studentGroups;

  @ManyToMany
  @JsonIgnore
  private List<Room> rooms;

  @ManyToMany
  @JsonIgnore
  private List<Subject> subjects;

  @ManyToMany
  @JsonIgnore
  private List<Grade> grades;

  @ManyToMany
  @JsonIgnore
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
}
