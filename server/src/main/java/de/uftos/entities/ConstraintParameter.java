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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database table for constraint parameters.
 * Contains an ID, the name and type of the parameter as well as the constraint signature using it.
 */
@Entity(name = "constraint_parameter")
@Data
@NoArgsConstructor
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

  public ConstraintParameter(String parameterName) {
    this.parameterName = parameterName;
  }
}
