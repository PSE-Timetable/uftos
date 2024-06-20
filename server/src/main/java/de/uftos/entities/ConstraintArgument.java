package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * The database entity for constraint arguments.
 * Contains an ID, the name and value of the argument as well as
 * the constraint instance using the argument.
 */
@Entity(name = "constraint_argument")
@Data
public class ConstraintArgument {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;
  private String value;

  @ManyToOne
  @JsonIgnore
  private ConstraintInstance constraintInstance;
}
