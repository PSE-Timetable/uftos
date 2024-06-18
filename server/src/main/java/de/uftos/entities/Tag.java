package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.Data;

@Entity(name = "tags")
@Data
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  @ManyToMany
  private List<Student> students;

  @ManyToMany
  private List<Teacher> teachers;

  @ManyToMany
  private List<StudentGroup> studentGroups;

  @ManyToMany
  private List<Room> rooms;

  @ManyToMany
  private List<Subject> subjects;

  @ManyToMany
  private List<Grade> grades;

  @ManyToMany
  private List<Timeslot> timeslots;
}
