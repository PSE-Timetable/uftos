package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
  @NotEmpty
  private String id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "constraint_parameter_id", nullable = false)
  private ConstraintParameter constraintParameter;

  @NotEmpty
  private String value;

  /**
   * Creates a new constraint argument.
   *
   * @param name  the of the parameter this argument refers to
   * @param value the id of the entity this argument refers to
   */
  public ConstraintArgument(String name, String value) {
    this.constraintParameter = new ConstraintParameter(name);
    this.value = value;
  }
}
