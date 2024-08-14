package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
  @NotEmpty
  private String id;

  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;
  @NotEmpty
  private String acronym;

  @NotNull
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "teachers_subjects",
      joinColumns = @JoinColumn(name = "teachers_id"),
      inverseJoinColumns = @JoinColumn(name = "subjects_id"))
  private List<Subject> subjects;

  @NotEmpty
  @ManyToMany(fetch = FetchType.EAGER)
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

  /**
   * Creates a new teacher.
   * Used if the ID is known.
   *
   * @param id the ID of the teacher.
   */
  public Teacher(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Teacher teacher = (Teacher) other;
    return Objects.equals(id, teacher.id);
  }
}
