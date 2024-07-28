package de.uftos.services;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.entities.ConstraintParameter;
import de.uftos.entities.ConstraintSignature;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.ucdl.UcdlRepository;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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

  /**
   * Sets the UCDL code and attempts to parse it.
   *
   * @param file  the file containing the new UCDL code.
   * @param force whether inconsistencies should be ignored and the ucdl file forcefully updated.
   * @return a response whether parsing the file was successful or not.
   */
  public ParsingResponse setUcdl(MultipartFile file, boolean force) {
    if (force) {
      return forceSetUcdl(file);
    }

    String ucdlCode;
    try {
      ucdlCode = new String(file.getBytes());
    } catch (IOException e) {
      return new ParsingResponse(false, e.getMessage());
    }

    List<ConstraintSignature> signatures = constraintSignatureRepository.findAll();
    HashMap<String, ConstraintDefinitionDto> definitions;
    try {
      definitions = ucdlRepository.getConstraintsFromString(ucdlCode);
    } catch (ParseException | IOException e) {
      return new ParsingResponse(false, e.getMessage());
    }

    for (ConstraintSignature signature : signatures) {
      ConstraintDefinitionDto definition = definitions.get(signature.getName());
      if (!signature.getInstances().isEmpty() && signatureChanged(signature, definition)) {
        return new ParsingResponse(false,
            "Signatures of constraints changed!"
                + " Constraint instances will be deleted if code is saved!");
      }
    }

    //no signatures changed
    try {
      ucdlRepository.setUcdl(ucdlCode);
    } catch (IOException e) {
      return new ParsingResponse(false, e.getMessage());
    }

    removeDeletedSignatures(signatures, definitions);
    saveDefinitionSignatures(signatures, definitions);

    return new ParsingResponse(true, "Code saved successfully!");
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
        if (signatureChanged(signature, definition)) {
          constraintSignatureRepository.delete(signature);
          signatures.remove(signature);
        }
      }

      saveDefinitionSignatures(signatures, definitions);

      return new ParsingResponse(true,
          "Saved file forcefully and deleted inconsistent constraint instances!");
    } catch (ParseException | IOException e) {
      return new ParsingResponse(true, "Saved file with invalid code forcefully!");
    }
  }

  private void saveDefinitionSignatures(List<ConstraintSignature> signatures,
                                        HashMap<String, ConstraintDefinitionDto> definitions) {
    for (ConstraintSignature signature : signatures) {
      definitions.remove(signature.getName());
    }

    for (ConstraintDefinitionDto definition : definitions.values()) {
      saveDefinitionSignature(definition);
    }
  }

  private void saveDefinitionSignature(ConstraintDefinitionDto definition) {

    List<ConstraintParameter> parameters = new ArrayList<>();
    for (Map.Entry<String, ResourceType> parameterEntry : definition.parameters()
        .sequencedEntrySet()) {
      ConstraintParameter parameter = new ConstraintParameter();
      parameter.setParameterName(parameterEntry.getKey());
      parameter.setParameterType(parameterEntry.getValue());
      parameters.add(parameter);
    }

    ConstraintSignature signature = new ConstraintSignature();
    signature.getParameters().addAll(parameters);

    constraintSignatureRepository.save(signature);
  }

  private void removeDeletedSignatures(List<ConstraintSignature> signatures,
                                       HashMap<String, ConstraintDefinitionDto> definitions) {
    for (ConstraintSignature signature : signatures) {
      if (definitions.get(signature.getName()) == null) {
        constraintSignatureRepository.delete(signature);
        signatures.remove(signature);
      }
    }
  }

  private boolean signatureChanged(ConstraintSignature signature,
                                   ConstraintDefinitionDto definition) {
    if (definition == null) {
      return true;
    }
    if (!definition.name().equals(signature.getName())) {
      throw new IllegalArgumentException();
    }

    Queue<ConstraintParameter> signatureParameters = new ArrayDeque<>(signature.getParameters());
    Queue<ResourceType> definitionParameters =
        new ArrayDeque<>(definition.parameters().sequencedValues());

    if (signatureParameters.size() != definitionParameters.size()) {
      return true;
    }

    while (!signatureParameters.isEmpty()) {
      if (signatureParameters.remove().getParameterType() != definitionParameters.remove()) {
        return true;
      }
    }

    return false;
  }

}
