package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database table for constraint arguments.
 * Contains an ID, the name and value of the argument as well as
 * the constraint instance using the argument.
 */
@Entity(name = "constraint_argument")
@Data
@NoArgsConstructor
public class ConstraintArgument {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;
  private String value;

  public ConstraintArgument(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ConstraintArgument that = (ConstraintArgument) other;
    return Objects.equals(id, that.id);
  }
}
