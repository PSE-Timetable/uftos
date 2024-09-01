package de.uftos.repositories.ucdl;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;

public class UcdlRepositoryImplTest {


  @Test
  void getUcdl() {
    assertThrows(BadRequestException.class, () -> new UcdlRepositoryImpl().getUcdl());
  }

  @Test
  void setUcdl() {
    assertThrows(BadRequestException.class, () -> new UcdlRepositoryImpl().setUcdl(""));
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
