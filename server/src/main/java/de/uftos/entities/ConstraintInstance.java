package de.uftos.entities;

import de.uftos.dto.solver.RewardPenalize;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import java.util.Map;

public class ConstraintInstance {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  private ConstraintSignature signature;

  @MapKey
  @ElementCollection
  private Map<String, String> arguments;

  private RewardPenalize type;
}
