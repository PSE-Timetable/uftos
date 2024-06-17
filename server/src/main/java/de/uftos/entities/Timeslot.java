package de.uftos.entities;

import de.uftos.dto.Weekday;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "timeslots")
@Data
public class Timeslot {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private long id;

  @Enumerated(EnumType.STRING)
  private Weekday day;
  private int slot;
}

