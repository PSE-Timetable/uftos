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
public class SubjectTimefoldInstanceTest {

  @Test
  void getResourcesType() {
    SubjectTimefoldInstance subject = new SubjectTimefoldInstance("test");
    assertEquals(subject.getResourceType(), ResourceType.SUBJECT);
  }
}