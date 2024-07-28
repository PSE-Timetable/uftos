package de.uftos.services;

import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.repositories.ucdl.UcdlRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
  public ParsingResponse validate(MultipartFile file) {
    try {
      return ucdlRepository.parseString(new String(file.getBytes()));
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not validate file");
    }
  }

  /**
   * Sets the UCDL code and attempts to parse it.
   *
   * @param file the file containing the new UCDL code.
   * @return a response whether parsing the file was successful or not.
   */
  public ParsingResponse setUcdl(MultipartFile file) {
    //todo: set String of repository and perform consistency-check
    return ucdlRepository.parseFile();
  }

  /**
   * Gets the current UCDL file.
   *
   * @return the current UCDL file.
   */
  public Resource getUcdl() {
    try {
      String content = ucdlRepository.getUcdl();
      return new InputStreamResource(
          new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UCDL file could not be loaded");
    }
  }

}
