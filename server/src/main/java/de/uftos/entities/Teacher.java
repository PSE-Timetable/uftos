package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

/**
 * The database entry for teachers.
 * Contains an ID, the first and last name, the acronym as well as tags and the subjects and
 * lessons they teach.
 */
@Entity
@Table(name = "teachers")
@Data
public class Teacher {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private long id;

  private String firstName;
  private String lastName;
  private String acronym;

  @ManyToMany
  private List<Subject> subjects;

  @ManyToMany
  private List<Tag> tags;

  @OneToMany
  private List<Lesson> lessons;
}
