package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * The database entity for server.
 * Contains an ID and a timeslot length.
 * There should only ever exist one entity of this kind.
 */
@Entity(name = "server")
@Data
public class Server {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private int timeslotLength;
}
