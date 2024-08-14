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
 * The database entity for subjects.
 * Contains an ID, the name of the subject as well as the teachers, tags and lessons associated
 * with it.
 */
@Entity(name = "subjects")
@Data
@NoArgsConstructor
public class Subject {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @NotEmpty
  private String id;

  @NotEmpty
  private String name;

  private String color;

  @JsonIgnore
  @ManyToMany(mappedBy = "subjects")
  private List<Teacher> teachers;

  @NotNull
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "subjects_tags",
      joinColumns = @JoinColumn(name = "subjects_id"),
      inverseJoinColumns = @JoinColumn(name = "tags_id"))
  private List<Tag> tags;

  @JsonIgnore
  @OneToMany(mappedBy = "subject")
  private List<Lesson> lessons;

  @JsonIgnore
  @OneToMany(mappedBy = "subject")
  private List<LessonsCount> lessonsCounts;

  /**
   * Creates a new subject.
   * Used if the ID is known.
   *
   * @param id the ID of the subject.
   */
  public Subject(String id) {
    this.id = id;
  }

  /**
   * Creates a new subject.
   * Used if the ID isn't known.
   *
   * @param name   the name of the subject.
   * @param color  the color of the subject.
   * @param tagIds the IDs of the tags associated with the subject.
   */
  public Subject(String name, String color, List<String> tagIds) {
    this.name = name;
    this.color = color;
    this.tags = tagIds.stream().map(Tag::new).toList();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Subject subject = (Subject) other;
    return Objects.equals(id, subject.id);
  }
}
