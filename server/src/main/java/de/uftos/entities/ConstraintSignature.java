package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Id;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Map;

public class ConstraintSignature {
  @Id
  private String name;

  private String description;
  private RewardPenalize defaultType;

  @MapKey
  @ElementCollection
  private Map<String, ResourceType> parameters;

  @OneToMany
  @JsonIgnore
  private List<ConstraintInstance> instances;
}
