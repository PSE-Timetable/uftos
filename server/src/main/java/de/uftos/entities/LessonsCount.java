package de.uftos.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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