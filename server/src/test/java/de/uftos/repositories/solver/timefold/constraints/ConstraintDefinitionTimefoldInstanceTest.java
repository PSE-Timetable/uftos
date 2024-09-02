package de.uftos.repositories.solver.timefold.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uftos.dto.solver.RewardPenalize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConstraintDefinitionTimefoldInstanceTest {

  @Test
  void constructor() {
    ConstraintDefinitionTimefoldInstance definition =
        new ConstraintDefinitionTimefoldInstance("test", RewardPenalize.HARD_PENALIZE, (l) -> true);
    assertEquals(definition.name(), "test");
    assertEquals(definition.defaultType(), RewardPenalize.HARD_PENALIZE);
    assertTrue(definition.evaluationFunction().apply(null));
  }
}
