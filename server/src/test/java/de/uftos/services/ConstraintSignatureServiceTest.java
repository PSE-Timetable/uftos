package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import de.uftos.dto.responsedtos.ConstraintInstancesResponseDto;
import de.uftos.entities.ConstraintSignature;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConstraintSignatureServiceTest {
  @Mock
  private ConstraintSignatureRepository constraintSignatureRepository;

  @InjectMocks
  private ConstraintSignatureService constraintSignatureService;

  @BeforeEach
  void setUp() {
    ConstraintSignature signature = new ConstraintSignature();
    signature.setName("test signature");

    when(constraintSignatureRepository.findById("test signature")).thenReturn(
        Optional.of(signature));
  }

  @Test
  void constrainSignatureById() {
    ConstraintSignature result =
        constraintSignatureService.getById("test signature");
    assertNotNull(result);
    assertEquals("test signature", result.getName());
  }

  @Test
  void constrainSignatureByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> constraintSignatureService.getById("non existent signature"));
  }
}
