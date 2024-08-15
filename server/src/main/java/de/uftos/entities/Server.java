package de.uftos.entities;

import de.uftos.utils.JsonbConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  private String currentYear;

  @Convert(converter = JsonbConverter.class)
  @Column(columnDefinition = "jsonb")
  private TimetableMetadata timetableMetadata;

  public Server(TimetableMetadata timetableMetadata, String currentYear) {
    this.timetableMetadata = timetableMetadata;
    this.currentYear = currentYear;
  }
}
