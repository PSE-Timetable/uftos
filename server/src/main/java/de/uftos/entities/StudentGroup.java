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

@Entity(name = "studentGroups")
@Data
@NoArgsConstructor
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
  @JsonIgnore
  private List<Lesson> lessons;

  public StudentGroup(String id) {
    this.id = id;
  }

  public StudentGroup(String name, List<String> studentIds, List<String> gradeIds,
                      List<String> tagIds) {
    this.name = name;
    this.students = studentIds.stream().map(Student::new).toList();
    this.grades = gradeIds.stream().map(Grade::new).toList();
    this.tags = tagIds.stream().map(Tag::new).toList();
  }
}
