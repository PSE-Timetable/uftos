package de.uftos.services;

import de.uftos.dto.parser.ParsingResponse;
import de.uftos.repositories.ucdl.UcdlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * The service for editing the UCDL-File.
 */
@Service
public class UcdlEditorService {
  private final UcdlRepository ucdlRepository;

  /**
   * Creates a UCDL editor service.
   *
   * @param ucdlRepository the repository for accessing the UCDL parser.
   */
  @Autowired
  public UcdlEditorService(UcdlRepository ucdlRepository) {
    this.ucdlRepository = ucdlRepository;
  }

  /**
   * Sets the UCDL code and attempts to parse it.
   *
   * @param file the file containing the new UCDL code.
   * @return a response whether parsing the file was successful or not.
   */
  public ParsingResponse setUcdl(MultipartFile file) {
    //todo: set String of repository
    return ucdlRepository.parseFile();
  }

  /**
   * Gets the current UCDL file.
   *
   * @return the current UCDL file.
   */
  public MultipartFile getUcdl() {
    //todo: return actual file
    return null;
  }

}
