package de.uftos.entities;

import de.uftos.dto.ResourceType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * The database table for constraint parameters.
 * Contains an ID, the name and type of the parameter as well as the constraint signature using it.
 */
@Entity(name = "constraint_parameter")
@Data
public class ConstraintParameter {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String parameterName;

  @Enumerated(EnumType.STRING)
  private ResourceType parameterType;
}
