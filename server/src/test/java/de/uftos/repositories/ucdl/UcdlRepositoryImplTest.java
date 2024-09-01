package de.uftos.repositories.ucdl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UcdlRepositoryImplTest {

  @BeforeEach
  void setup() throws IOException {
    when(Files.readString(any())).thenReturn("");
  }

  @Test
  void getUcdl() {
    assertDoesNotThrow(() -> new UcdlRepositoryImpl().getUcdl());
  }

  @Test
  void setUcdl() {
    assertDoesNotThrow(() -> new UcdlRepositoryImpl().setUcdl(""));
  }

  @Test
  void getDefaultUcdl() {
    assertDoesNotThrow(() -> new UcdlRepositoryImpl().getDefaultUcdl());
  }

  @Test
  void parseFile() {
    assertDoesNotThrow(() -> new UcdlRepositoryImpl().parseFile());
  }

  @Test
  void parseString() {
    assertDoesNotThrow(() -> new UcdlRepositoryImpl().parseString(""));
  }

  @Test
  void getConstraints() {
    assertDoesNotThrow(() -> new UcdlRepositoryImpl().getConstraints());
  }

  @Test
  void getConstraintsFromString() {
    assertDoesNotThrow(() -> new UcdlRepositoryImpl().getConstraintsFromString(""));
  }
}
