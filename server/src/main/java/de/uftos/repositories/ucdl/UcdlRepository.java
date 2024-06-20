package de.uftos.repositories.ucdl;

import de.uftos.dto.parser.ConstraintDefinitionDto;
import de.uftos.dto.parser.ParsingResponse;
import java.text.ParseException;
import java.util.HashMap;

/**
 * Manages the UCDL-file.
 */
public interface UcdlRepository {

  /**
   * Gets the content of the UCDL-file.
   *
   * @return the UCDL-code contained in the UCDL-file.
   */
  String getUcdl();

  /**
   * Sets the content of the UCDL-file.
   *
   * @param ucdl the new content of the UCDL-file
   */
  void setUcdl(String ucdl);

  /**
   * Attempts to parse the UCDL-file and returns whether parsing was successful or not.
   *
   * @return a ParsingResponse containing information about whether parsing was successful or not.
   */
  ParsingResponse parseFile();

  /**
   * Parses the UCDL-file and returns all constraint definitions contained by the file.
   *
   * @return a HashMap which allows for finding constraint definitions by their name.
   * @throws ParseException when the file can't be parsed.
   */
  HashMap<String, ConstraintDefinitionDto> getConstraints() throws ParseException;
}
