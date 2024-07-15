package de.uftos.entities;

import de.uftos.dto.LessonsCountRequestDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
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

  @NotEmpty
  private String name;

  //multiple curricula for different years can exist for the same grade
  @ManyToOne
  private Grade grade;

  @OneToMany(cascade = CascadeType.REMOVE)
  private List<LessonsCount> lessonsCounts;

  /**
   * Creates a new curriculum.
   *
   * @param gradeId       the grade id to which the curriculum applies to.
   * @param name          the name given to the curriculum.
   * @param lessonsCounts the lesson counts which apply to the given grade.
   */
  public Curriculum(String gradeId, String name, List<LessonsCountRequestDto> lessonsCounts) {
    this.name = name;
    this.grade = new Grade(gradeId);
    this.lessonsCounts = lessonsCounts.stream().map(LessonsCountRequestDto::map).toList();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Curriculum that = (Curriculum) other;
    return Objects.equals(id, that.id);
  }
}
