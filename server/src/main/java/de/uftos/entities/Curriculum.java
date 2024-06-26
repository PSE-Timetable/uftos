package de.uftos.entities;

import de.uftos.dto.LessonsCountRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database table for curriculums.
 * Contains an ID, a grade to which the lessons are applied to and a list of lesson counts.
 */
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

  /**
   * Creates a new curriculum.
   *
   * @param gradeId       the grade id to which the curriculum applies to.
   * @param lessonsCounts the lesson counts which apply to the given grade.
   */
  public Curriculum(String gradeId, List<LessonsCountRequestDto> lessonsCounts) {
    // TODO implement
  }
}
