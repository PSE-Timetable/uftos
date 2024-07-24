package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uftos.dto.ResourceType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
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
  @NotEmpty
  private String id;

  @NotEmpty
  private String parameterName;

  @JsonIgnore
  @OneToMany(mappedBy = "constraintParameter")
  private List<ConstraintArgument> constraintArguments;

  @NotEmpty
  @Enumerated(EnumType.STRING)
  private ResourceType parameterType;

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ConstraintParameter that = (ConstraintParameter) other;
    return Objects.equals(id, that.id);
  }
}
