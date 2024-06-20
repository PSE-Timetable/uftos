package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

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
