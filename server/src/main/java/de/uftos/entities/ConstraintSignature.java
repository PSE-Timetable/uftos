package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uftos.dto.solver.RewardPenalize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import lombok.Data;

/**
 * The database table for constraint signatures.
 * Contains an ID, the name of the grade as well as the student groups that in the
 * grade as well as the tags associated with it.
 */
@Entity(name = "constraint_signature")
@Data
public class ConstraintSignature {
  @Id
  @NotEmpty
  private String name;

  @NotEmpty
  private String description;
 
  @NotEmpty
  private RewardPenalize defaultType;

  @NotNull
  @OneToMany(cascade = CascadeType.ALL)
  private List<ConstraintParameter> parameters;

  @JsonIgnore
  @OneToMany(mappedBy = "signature", cascade = CascadeType.ALL)
  private List<ConstraintInstance> instances;

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ConstraintSignature that = (ConstraintSignature) other;
    return Objects.equals(name, that.name);
  }
}
