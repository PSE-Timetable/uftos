package de.uftos.entities;

import de.uftos.dto.solver.RewardPenalize;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;

@Entity(name = "constraint_instance")
@Data
public class ConstraintInstance {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  private ConstraintSignature signature;

  @OneToMany
  private List<ConstraintArgument> arguments;

  private RewardPenalize type;
}