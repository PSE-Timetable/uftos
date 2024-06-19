package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entry for teachers.
 * Contains an ID, the first and last name, the acronym as well as tags and the subjects and
 * lessons they teach.
 */
@Entity(name = "teachers")
@Data
@NoArgsConstructor
public class Teacher {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String firstName;
  private String lastName;
  private String acronym;

  @ManyToMany
  private List<Subject> subjects;

  @ManyToMany
  private List<Tag> tags;

  @OneToMany
  @JsonIgnore
  private List<Lesson> lessons;

  public Teacher(String firstName, String lastName, String acronym, List<String> subjectIds,
                 List<String> tagIds) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.acronym = acronym;
    this.subjects = subjectIds.stream().map(Subject::new).toList();
    this.tags = tagIds.stream().map(Tag::new).toList();
  }
}
