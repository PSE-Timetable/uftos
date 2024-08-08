package de.uftos.repositories.solver.timefold.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.uftos.dto.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NumberTimefoldInstanceTest {

  @Test
  void getResourcesType() {
    NumberTimefoldInstance number = new NumberTimefoldInstance(0);
    assertEquals(number.getResourceType(), ResourceType.NUMBER);
  }

  @Test
  void getId() {
    NumberTimefoldInstance number;

    number = new NumberTimefoldInstance(0);
    assertEquals(number.getId(), "number:0");

    number = new NumberTimefoldInstance(Integer.MIN_VALUE);
    assertEquals(number.getId(), "number:" + Integer.MIN_VALUE);

    number = new NumberTimefoldInstance(Integer.MAX_VALUE);
    assertEquals(number.getId(), "number:" + Integer.MAX_VALUE);
  }
}
