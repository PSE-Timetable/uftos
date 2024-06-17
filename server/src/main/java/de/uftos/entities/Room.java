package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "rooms")
@Data
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private long id;

  private String name;
  private String buildingName;
  private int capacity;
}
