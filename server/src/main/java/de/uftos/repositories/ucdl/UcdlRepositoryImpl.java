package de.uftos.repositories.ucdl;

import de.uftos.dto.SuccessResponse;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.repositories.ucdl.parser.UcdlParser;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Repository;

/**
 * Manages the UCDL-file.
 */
@Repository
public class UcdlRepositoryImpl implements UcdlRepository {
  private static final Path UCDL_PATH = Paths.get("/app/ucdl/ucdl.yml");
  private HashMap<String, ConstraintDefinitionDto> currentDefinitions = null;

  @Override
  public String getUcdl() throws BadRequestException {
    try {
      Files.createFile(UCDL_PATH);
    } catch (FileAlreadyExistsException e) {
      //everything is fine here
    } catch (IOException e) {
      throw new BadRequestException(e);
    }
    try {
      return Files.readString(UCDL_PATH);
    } catch (IOException e) {
      throw new BadRequestException(e);
    }
  }

  @Override
  public void setUcdl(String ucdl) throws BadRequestException {
    this.currentDefinitions = null;
    try {
      Files.writeString(UCDL_PATH, ucdl);
    } catch (IOException e) {
      throw new BadRequestException(e);
    }
  }

  @Override
  public SuccessResponse parseFile() {
    try {
      return parseString(this.getUcdl(), true);
    } catch (BadRequestException e) {
      return new SuccessResponse(false, e.getMessage());
    }
  }

  @Override
  public SuccessResponse parseString(String input) {
    return parseString(input, false);
  }

  private SuccessResponse parseString(String input, boolean doesSaveDefinitions) {
    try {
      if (doesSaveDefinitions) {
        this.currentDefinitions = UcdlParser.getDefinitions(input);
      } else {
        UcdlParser.getDefinitions(input);
      }
    } catch (ParseException e) {
      return new SuccessResponse(false, e.getMessage());
    }
    return new SuccessResponse(true, "Parsing was successful!");
  }

  @Override
  public HashMap<String, ConstraintDefinitionDto> getConstraints() {
    if (this.currentDefinitions == null && !this.parseFile().success()) {
      return null;
    }
    return this.currentDefinitions;
  }

  @Override
  public HashMap<String, ConstraintDefinitionDto> getConstraintsFromString(String input)
      throws ParseException {
    return UcdlParser.getDefinitions(input);
  }
}
