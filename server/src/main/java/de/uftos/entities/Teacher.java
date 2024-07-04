package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entity for teachers.
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
  @JoinTable(name = "teachers_subjects",
      joinColumns = @JoinColumn(name = "teachers_id"),
      inverseJoinColumns = @JoinColumn(name = "subjects_id"))
  private List<Subject> subjects;

  @ManyToMany
  @JoinTable(name = "teachers_tags",
      joinColumns = @JoinColumn(name = "teachers_id"),
      inverseJoinColumns = @JoinColumn(name = "tags_id"))
  private List<Tag> tags;

  @JsonIgnore
  @OneToMany(mappedBy = "teacher")
  private List<Lesson> lessons;

  /**
   * Creates a new teacher.
   *
   * @param firstName  the first name of the teacher.
   * @param lastName   the last name of the teacher.
   * @param acronym    the acronym used for the teacher.
   * @param subjectIds the IDs of subjects the teacher teaches.
   * @param tagIds     the IDs of tags associated with the teacher.
   */
  public Teacher(String firstName, String lastName, String acronym, List<String> subjectIds,
                 List<String> tagIds) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.acronym = acronym;
    this.subjects = subjectIds.stream().map(Subject::new).toList();
    this.tags = tagIds.stream().map(Tag::new).toList();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName, acronym);
  }
}
