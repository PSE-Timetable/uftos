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

@Entity
@Table(name = "studentGroups")
@Data
public class StudentGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  @ManyToMany
  private List<Student> students;

  @ManyToMany
  private List<Grade> grades;

  @ManyToMany
  private List<Tag> tags;

  @OneToMany
  private List<Lesson> lessons;
}
