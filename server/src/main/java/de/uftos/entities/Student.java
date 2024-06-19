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
 * The database entity for students.
 * Contains an ID, the first and last name as well as tags associated with the student and the
 * student groups which they are a part of.
 */
@Entity(name = "students")
@Data
@NoArgsConstructor
public class Student {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  private String firstName;
  private String lastName;

  @ManyToMany
  @JsonIgnore
  private List<StudentGroup> groups;

  @ManyToMany
  private List<Tag> tags;

  /**
   * Creates a new student.
   * Used if the ID is known.
   *
   * @param id the ID of the student.
   */
  public Student(String id) {
    this.id = id;
  }

  /**
   * Creates a new student.
   * Used if the ID isn't known.
   *
   * @param firstName the first name of the student.
   * @param lastName  the last name of the student.
   * @param tagIds    the IDs of the tags associated with the student.
   */
  public Student(String firstName, String lastName, List<String> tagIds) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.tags = tagIds.stream().map(Tag::new).toList();
  }
}
