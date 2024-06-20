package de.uftos.repositories.ucdl;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;

public interface ucdlRepository {

  /**
   * Attempts to parse the given File and returns whether parsing was successful or not.
   * @param file the file which gets parsed.
   * @return a ParsingResponse containing information about whether parsing was successful or not.
   */
  //ParsingResponse parseFile(File file);

  /**
   * Parses the file and returns all constraint definitions contained by the file.
   * @param file the file containing the constraint definitions.
   * @return a HashMap which allows for finding constraint definitions by their name.
   * @throws ParseException when the file can't be parsed.
   */
  //HashMap<String, ConstraintDefinition> getConstraints(File file) throws ParseException;
}
