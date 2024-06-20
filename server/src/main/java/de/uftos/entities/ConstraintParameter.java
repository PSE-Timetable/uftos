package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uftos.dto.ResourceType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * The database entity for constraint parameters.
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

  @ManyToOne
  @JsonIgnore
  private ConstraintSignature constraintSignature;
}
