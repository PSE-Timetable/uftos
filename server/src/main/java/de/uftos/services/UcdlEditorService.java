package de.uftos.services;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.entities.ConstraintSignature;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.ucdl.UcdlRepository;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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
  private final ConstraintSignatureRepository constraintSignatureRepository;

  /**
   * Creates a UCDL editor service.
   *
   * @param ucdlRepository the repository for accessing the UCDL parser.
   */
  @Autowired
  public UcdlEditorService(UcdlRepository ucdlRepository,
                           ConstraintSignatureRepository constraintSignatureRepository) {
    this.ucdlRepository = ucdlRepository;
    this.constraintSignatureRepository = constraintSignatureRepository;
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
   * @param file  the file containing the new UCDL code.
   * @param force whether inconsistencies should be ignored and the ucdl file forcefully updated.
   * @return a response whether parsing the file was successful or not.
   */
  //TODO: Wait for Solver and Constraint branches to be merged as a lot of code is currently missing
  public ParsingResponse setUcdl(MultipartFile file, boolean force) {
    if (force) {
      return forceSetUcdl(file);
    }
    //todo: set String of repository (without force)
    return ucdlRepository.parseFile();
  }

  private ParsingResponse forceSetUcdl(MultipartFile file) {
    try {
      String ucdl = new String(file.getBytes());
      ucdlRepository.setUcdl(ucdl);
    } catch (IOException e) {
      return new ParsingResponse(false, e.getMessage());
    }

    try {
      HashMap<String, ConstraintDefinitionDto> definitions = ucdlRepository.getConstraints();

      List<ConstraintSignature> signatures = constraintSignatureRepository.findAll();

      for (ConstraintSignature signature : signatures) {
        ConstraintDefinitionDto definition = definitions.get(signature.getName());

        //constraint with this signature got deleted or signature got changed
        if (definition == null
            || definition.parameters().size() != signature.getParameters().size()) {
          constraintSignatureRepository.delete(signature);
        }
        for (int i = 0; i < signature.getParameters().size(); i++) {
          //todo (once the missing code is available)
          ResourceType signatureParameterType = signature.getParameters().get(i).getParameterType();
          ResourceType definitionParameterType = definition.parameters().get("this");
          if (signatureParameterType != definitionParameterType) {
            constraintSignatureRepository.delete(signature);

            ConstraintSignature updatedSignature = new ConstraintSignature();

            break;
          }
        }
      }
      return new ParsingResponse(true,
          "Saved file forcefully and deleted inconsistent constraint instances!");
    } catch (ParseException | IOException e) {
      return new ParsingResponse(true, "Saved file with invalid code forcefully!");
    }
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
