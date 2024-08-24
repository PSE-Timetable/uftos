package de.uftos.repositories.ucdl;

import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.IOException;
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
  String getUcdl() throws IOException;

  /**
   * Sets the content of the UCDL-file.
   *
   * @param ucdl the new content of the UCDL-file
   */
  void setUcdl(String ucdl) throws IOException;

  /**
   * Gets the default UCDL-file.
   *
   * @return the default UCDL-code.
   */
  String getDefaultUcdl();

  /**
   * Attempts to parse the UCDL-file and returns whether parsing was successful or not.
   *
   * @return a ParsingResponse containing information about whether parsing was successful or not.
   */
  ParsingResponse parseFile();

  /**
   * Attempts to parse the given input and returns whether parsing was successful or not.
   *
   * @return a ParsingResponse containing information about whether parsing was successful or not.
   */
  ParsingResponse parseString(String input);

  /**
   * Parses the UCDL-file and returns all constraint definitions contained by the file.
   *
   * @return a HashMap which allows for finding constraint definitions by their name.
   * @throws ParseException when the file can't be parsed.
   */
  HashMap<String, ConstraintDefinitionDto> getConstraints() throws ParseException, IOException;

  /**
   * Parses the given String and returns all constraint definitions contained by it.
   *
   * @param input the String which gets parsed.
   * @return a HashMap which allows for finding constraint definitions by their name.
   * @throws ParseException when the String can't be parsed.
   */
  HashMap<String, ConstraintDefinitionDto> getConstraintsFromString(String input)
      throws ParseException;
}
