package de.uftos.utils;

import de.uftos.entities.ConstraintArgument;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * A class that deletes all instances of constraints that contain a soon-to-be deleted entity
 * as an argument.
 */
public class ConstraintInstanceDeleter {

  private final ConstraintSignatureRepository constraintSignatureRepository;
  private final ConstraintInstanceRepository constraintInstanceRepository;

  /**
   * Creates the constraint instance deleter.
   *
   * @param constraintSignatureRepository the repository for accessing the constraint signature table.
   * @param constraintInstanceRepository  the repository for accessing the constraint instance table.
   */
  public ConstraintInstanceDeleter(ConstraintSignatureRepository constraintSignatureRepository,
                                   ConstraintInstanceRepository constraintInstanceRepository) {
    this.constraintSignatureRepository = constraintSignatureRepository;
    this.constraintInstanceRepository = constraintInstanceRepository;
  }

  /**
   * Deletes all the instances containing an argument with these ids.
   *
   * @param ids the ids of the constraint arguments
   */
  public void removeAllInstancesWithArgumentValue(String[] ids) {
    Specification<ConstraintSignature> signatureSpecificationBuilder =
        new SpecificationBuilder<ConstraintSignature>()
            .andDoubleJoinIn(ids, "instances", "arguments", "value")
            .build();
    List<ConstraintSignature> constraintSignatures =
        constraintSignatureRepository.findAll(signatureSpecificationBuilder);

    List<ConstraintInstance> constraintInstances = new ArrayList<>(constraintSignatures.stream()
        .flatMap(constraintSignature -> constraintSignature.getInstances().stream()).toList());

    List<String> resourceIds = Arrays.stream(ids).toList();
    constraintInstances.removeIf(constraintInstance -> {
      List<ConstraintArgument> args = constraintInstance.getArguments();
      for (String argumentId : args.stream().map(ConstraintArgument::getValue).toList()) {
        if (resourceIds.contains(argumentId)) {
          return false;
        }
      }
      return true;
    });

    for (ConstraintSignature constraintSignature : constraintSignatures) {
      constraintSignature.getInstances().removeAll(constraintInstances);
    }

    this.constraintSignatureRepository.saveAll(constraintSignatures);
    this.constraintInstanceRepository.deleteAll(constraintInstances);
  }
}
