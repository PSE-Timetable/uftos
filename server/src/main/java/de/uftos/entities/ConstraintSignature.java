package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uftos.dto.solver.RewardPenalize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

  @NotNull
  private RewardPenalize defaultType;

  @NotNull
  @OneToMany(cascade = CascadeType.ALL)
  private List<ConstraintParameter> parameters = new ArrayList<>();

  @JsonIgnore
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ConstraintInstance> instances = new ArrayList<>();


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

  @Override
  public int hashCode() {
    int initialOddNumber = 293;
    int multiplierOddNumber = 307;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(name)
        .append(description)
        .append(defaultType)
        .toHashCode();
  }

}
