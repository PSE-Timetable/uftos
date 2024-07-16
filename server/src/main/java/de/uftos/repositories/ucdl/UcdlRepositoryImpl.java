package de.uftos.repositories.ucdl;

import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.repositories.ucdl.parser.UcdlParser;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.springframework.stereotype.Repository;

/**
 * Manages the UCDL-file.
 */
@Repository
public class UcdlRepositoryImpl implements UcdlRepository {
  private static final Path UCDL_PATH = Paths.get("/app/ucdl/ucdl.yml");
  private HashMap<String, ConstraintDefinitionDto> currentDefinitions = null;

  @Override
  public String getUcdl() throws IOException {
    Files.createFile(UCDL_PATH);
    return Files.readString(UCDL_PATH);
  }

  @Override
  public void setUcdl(String ucdl) throws IOException {
    this.currentDefinitions = null;
    Files.writeString(UCDL_PATH, ucdl);
  }

  @Override
  public ParsingResponse parseFile() {
    try {
      this.setCurrentDefinitions();
    } catch (ParseException | IOException e) {
      return new ParsingResponse(false, e.getMessage());
    }
    return new ParsingResponse(true, "Parsing was successful!");
  }

  @Override
  public HashMap<String, ConstraintDefinitionDto> getConstraints() {
    if (this.currentDefinitions == null && !this.parseFile().success()) {
      return null;
    }
    return this.currentDefinitions;
  }

  private void setCurrentDefinitions() throws IOException,
      de.uftos.repositories.ucdl.parser.javacc.ParseException {
    this.currentDefinitions = UcdlParser.getDefinitions(this.getUcdl());
  }
}
