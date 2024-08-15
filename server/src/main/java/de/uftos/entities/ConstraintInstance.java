package de.uftos.entities;

import de.uftos.dto.solver.RewardPenalize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
  @NotEmpty
  private String id;

  @NotNull
  @OneToMany(cascade = CascadeType.ALL)
  private List<ConstraintArgument> arguments;

  @NotNull
  private RewardPenalize type;
}
