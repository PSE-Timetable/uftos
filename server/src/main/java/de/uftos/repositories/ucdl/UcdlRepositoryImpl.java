package de.uftos.repositories.ucdl;

import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.repositories.ucdl.parser.UcdlParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import org.springframework.stereotype.Repository;

/**
 * Manages the UCDL-file.
 */
@Repository
public class UcdlRepositoryImpl implements UcdlRepository {
  private HashMap<String, ConstraintDefinitionDto> currentDefinitions = null;
  private File ucdlFile = new File("/app/ucdl/ucdl.yml");


  @Override
  public String getUcdl() {
    try {
      ucdlFile.createNewFile();
      FileReader reader = new FileReader(ucdlFile);
      int readInformation = reader.read();
      StringBuilder sb = new StringBuilder();
      while (readInformation >= 0) {
        sb.append((char) readInformation);
        readInformation = reader.read();
      }
      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setUcdl(String ucdl) {
    this.currentDefinitions = null;
    try {
      FileWriter writer = new FileWriter(ucdlFile);
      writer.write(ucdl);
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

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
  public HashMap<String, ConstraintDefinitionDto> getConstraints()
      throws ParseException, IOException {
    if (currentDefinitions == null) {
      this.setCurrentDefinitions();
    }
    return this.currentDefinitions;
  }

  private void setCurrentDefinitions() throws ParseException, IOException {
    this.currentDefinitions = UcdlParser.getDefinitions(this.getUcdl());
  }
}
