package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entity for grades.
 * Contains an ID, the name of the grade as well as the student groups that are in the
 * grade as well as the tags associated with it.
 */
@Entity(name = "grades")
@Data
@NoArgsConstructor
public class Grade {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  @ManyToMany
  private List<StudentGroup> studentGroups;

  @ManyToMany
  private List<Tag> tags;

  public Grade(String id) {
    this.id = id;
  }

  /**
   * Creates a new grade.
   *
   * @param name            the name of the grade.
   * @param studentGroupIds the IDs of the student groups that are in the grade.
   * @param tagIds          the IDs of the tags associated with the grade.
   */
  public Grade(String name, List<String> studentGroupIds, List<String> tagIds) {
    this.name = name;
    this.studentGroups = studentGroupIds.stream().map(StudentGroup::new).toList();
    this.tags = tagIds.stream().map(Tag::new).toList();
  }
}
