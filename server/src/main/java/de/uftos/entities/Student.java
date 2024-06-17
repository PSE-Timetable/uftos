package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

/**
 * The database entry for students.
 * Contains an ID, the first and last name as well as tags associated with the student and the
 * student groups which they are a part of.
 */
@Entity
@Table(name = "students")
@Data
public class Student {

  @ManyToMany
  private List<StudentGroup> groups;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String firstName;
  private String lastName;

  @ManyToMany
  private List<Tag> tags;
}
