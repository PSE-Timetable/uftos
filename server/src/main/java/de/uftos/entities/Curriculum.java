package de.uftos.entities;

import de.uftos.dto.requestdtos.LessonsCountRequestDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

  @OneToOne
  @JoinColumn(name = "grades_id", nullable = false)
  private Grade grade;

  @OneToMany(cascade = CascadeType.ALL)
  private List<LessonsCount> lessonsCounts = new ArrayList<>();

  /**
   * Creates a new curriculum.
   *
   * @param grade         the grade to which the curriculum applies to.
   * @param name          the name given to the curriculum.
   * @param lessonsCounts the lesson counts which apply to the given grade.
   */
  public Curriculum(Grade grade, String name, List<LessonsCountRequestDto> lessonsCounts) {
    this.name = name;
    this.grade = grade;
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

  @Override
  public int hashCode() {
    int initialOddNumber = 79;
    int multiplierOddNumber = 163;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(name)
        .toHashCode();
  }
}
