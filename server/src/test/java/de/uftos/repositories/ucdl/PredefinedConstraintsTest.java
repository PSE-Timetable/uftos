package de.uftos.repositories.ucdl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import de.uftos.repositories.ucdl.parser.PredefinedConstraint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PredefinedConstraintsTest {

  @Test
  void predefinedConstraint() {
    assertDoesNotThrow(PredefinedConstraint::values);
  }
}
