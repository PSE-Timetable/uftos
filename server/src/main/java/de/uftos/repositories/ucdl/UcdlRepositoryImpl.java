package de.uftos.repositories.ucdl;

import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.repositories.ucdl.parser.UcdlParser;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.stereotype.Repository;

/**
 * Manages the UCDL-file.
 */
@Repository
public class UcdlRepositoryImpl implements UcdlRepository {
  private final File ucdlFile = new File("/app/ucdl/ucdl.yml");
  private HashMap<String, ConstraintDefinitionDto> currentDefinitions = null;

  //todo: use java.nio
  @Override
  public String getUcdl() throws IOException {
    this.ucdlFile.createNewFile();
    FileReader reader = new FileReader(this.ucdlFile);
    int readInformation = reader.read();
    StringBuilder sb = new StringBuilder();
    while (readInformation >= 0) {
      sb.append((char) readInformation);
      readInformation = reader.read();
    }
    return sb.toString();
  }

  @Override
  public void setUcdl(String ucdl) throws IOException {
    this.currentDefinitions = null;
    FileWriter writer = new FileWriter(ucdlFile);
    writer.write(ucdl);
    writer.close();

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
