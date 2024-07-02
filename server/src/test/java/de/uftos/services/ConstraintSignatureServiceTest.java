package de.uftos.services;

import de.uftos.repositories.database.ConstraintSignatureRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ConstraintSignatureServiceTest {
  private static AutoCloseable mocks;
  @Mock
  private ConstraintSignatureRepository mockRepository;
  @InjectMocks
  private ConstraintSignatureService constraintSignatureService;

  @BeforeAll
  public static void beforeAll() {
    mocks = MockitoAnnotations.openMocks(ConstraintSignatureService.class);
  }

  @AfterAll
  public static void afterAll() throws Exception {
    mocks.close();
  }
}
