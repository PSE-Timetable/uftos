package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entity for server.
 * Contains an ID and a timeslot length.
 * There should only ever exist one entity of this kind.
 */
@Entity(name = "server")
@Data
@NoArgsConstructor
public class Server {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private int timeslotLength;

  private String currentYear;

  public Server(int timeslotLength, String currentYear) {
    this.timeslotLength = timeslotLength;
    this.currentYear = currentYear;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Server server = (Server) other;
    return Objects.equals(id, server.id);
  }
}
