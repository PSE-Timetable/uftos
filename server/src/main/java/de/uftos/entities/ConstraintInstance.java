package de.uftos.entities;

import de.uftos.dto.solver.RewardPenalize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Objects;
import lombok.Data;

/**
 * The database table for constraint instances.
 * Contains an ID, the signature of the corresponding constraint definition, a list of constraint
 * arguments as well as the information whether the instance gets hard/soft penalized/rewarded.
 */
@Entity(name = "constraint_instance")
@Data
public class ConstraintInstance {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  private ConstraintSignature signature;

  @OneToMany(cascade = CascadeType.ALL)
  private List<ConstraintArgument> arguments;

  private RewardPenalize type;

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    ConstraintInstance that = (ConstraintInstance) other;
    return Objects.equals(id, that.id);
  }
}
