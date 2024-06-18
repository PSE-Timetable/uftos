package de.uftos.entities;

import de.uftos.dto.Weekday;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table("timeslots")
@Data
public class Timeslot {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  private Weekday day;
  private int slot;

  @ManyToMany
  private List<Tag> tags;

  @OneToMany
  private List<Lesson> lessons;
}

