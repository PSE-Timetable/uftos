package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database table for lesson counts.
 * Contains an ID, the subject it applies to and a count for the amount of lessons.
 */
@Entity(name = "lessons_count")
@Data
@NoArgsConstructor
public class LessonsCount {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  @ManyToOne
  private Subject subject;
  private Integer count;

  /**
   * Creates a new lesson count.
   *
   * @param subjectId the id of the subject.
   * @param count     the number of timeslots this subject will be hold per week.
   */
  public LessonsCount(String subjectId, int count) {
    // TODO implement
  }
}