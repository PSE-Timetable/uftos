package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uftos.dto.solver.RewardPenalize;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
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
  private String name;

  private String description;
  private RewardPenalize defaultType;

  @OneToMany
  private List<ConstraintParameter> parameters;

  @OneToMany
  @JsonIgnore
  private List<ConstraintInstance> instances;
}
