package de.uftos.entities;

import de.uftos.utils.JsonbConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

  private String email;

  @Convert(converter = JsonbConverter.class)
  @Column(columnDefinition = "jsonb")
  private TimetableMetadata timetableMetadata;

  /**
   * Creates a new server config.
   *
   * @param timetableMetadata the timetable metadata, including information like start of the day.
   * @param currentYear       the current year.
   * @param email             the email address notifications should be send to.
   */
  public Server(TimetableMetadata timetableMetadata, String currentYear, String email) {
    this.timetableMetadata = timetableMetadata;
    this.currentYear = currentYear;
    this.email = email;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Server that = (Server) other;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int initialOddNumber = 167;
    int multiplierOddNumber = 277;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(currentYear)
        .toHashCode();
  }
}
