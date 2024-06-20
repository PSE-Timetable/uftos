package de.uftos.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entity for lesson counts.
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
}