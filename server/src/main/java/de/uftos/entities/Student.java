package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
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
  @NotEmpty
  private String id;
  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;

  @JsonIgnore
  @ManyToMany(mappedBy = "students")
  private List<StudentGroup> groups;

  @NotNull
  @ManyToMany
  @JoinTable(name = "students_tags",
      joinColumns = @JoinColumn(name = "students_id"),
      inverseJoinColumns = @JoinColumn(name = "tags_id"))
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

  /**
   * Creates a new student.
   * Used if the ID is known.
   *
   * @param firstName the first name of the student.
   * @param lastName  the last name of the student.
   * @param groups    the list of student groups in which the student is.
   * @param tags      the list of tags associated with the student.
   */
  public Student(String id, String firstName, String lastName, List<StudentGroup> groups,
                 List<Tag> tags) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.groups = groups;
    this.tags = tags;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Student student = (Student) other;
    return Objects.equals(id, student.id);
  }
}
