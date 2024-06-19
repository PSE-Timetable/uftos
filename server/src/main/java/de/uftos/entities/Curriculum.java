package de.uftos.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "curriculum")
@Data
@NoArgsConstructor
public class Curriculum {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  @OneToOne
  private Grade grade;
  @ManyToMany
  private List<LessonsCount> lessonsCounts;

  public Curriculum(Grade grade, List<LessonsCount> lessonsCounts) {
    this.grade = grade;
    this.lessonsCounts = lessonsCounts;
  }
}
