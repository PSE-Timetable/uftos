package de.uftos.repositories.ucdl;

import de.uftos.dto.parser.ConstraintDefinitionDto;
import de.uftos.dto.parser.ParsingResponse;
import java.text.ParseException;
import java.util.HashMap;

/**
 * Manages the UCDL-file.
 */
public class UcdlRepositoryImpl implements UcdlRepository {

  @Override
  public String getUcdl() {
    return "";
  }

  @Override
  public void setUcdl(String ucdl) {

  }

  @Override
  public ParsingResponse parseFile() {
    return null;
  }

  @Override
  public HashMap<String, ConstraintDefinitionDto> getConstraints() throws ParseException {
    return null;
  }
}
