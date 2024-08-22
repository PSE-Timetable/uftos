package de.uftos.services;

import de.uftos.dto.ResourceType;
import de.uftos.dto.SuccessResponse;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
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
  public SuccessResponse validate(MultipartFile file) {
    try {
      return this.ucdlRepository.parseString(new String(file.getBytes()));
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
      String content = this.ucdlRepository.getUcdl();
      return new InputStreamResource(
          new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UCDL file could not be loaded");
    }
  }

  /**
   * Gets the default UCDL file.
   *
   * @return the default UCDL file.
   */
  public Resource getDefaultUcdl() {
    String content = this.ucdlRepository.getDefaultUcdl();
    return new InputStreamResource(
        new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * Sets the UCDL code and attempts to parse it.
   *
   * @param file  the file containing the new UCDL code.
   * @param force whether inconsistencies should be ignored and the ucdl file forcefully updated.
   * @return a response whether parsing the file was successful or not.
   */
  public SuccessResponse setUcdl(MultipartFile file, boolean force) {
    if (force) {
      return forceSetUcdl(file);
    }

    String ucdlCode;
    try {
      ucdlCode = new String(file.getBytes());
    } catch (IOException e) {
      return new SuccessResponse(false, e.getMessage());
    }

    List<ConstraintSignature> signatures = constraintSignatureRepository.findAll();
    HashMap<String, ConstraintDefinitionDto> definitions;
    try {
      definitions = ucdlRepository.getConstraintsFromString(ucdlCode);
    } catch (ParseException e) {
      return new SuccessResponse(false, e.getMessage());
    }

    for (ConstraintSignature signature : signatures) {
      ConstraintDefinitionDto definition = definitions.get(signature.getName());
      if (!signature.getInstances().isEmpty() && signatureChanged(signature, definition)) {
        return new SuccessResponse(false,
            "Signatures of constraints changed!"
                + " Constraint instances will be deleted if code is saved!");
      }
    }

    //no signatures with existing instances changed
    try {
      ucdlRepository.setUcdl(ucdlCode);
    } catch (IOException e) {
      return new SuccessResponse(false, e.getMessage());
    }

    removeDeletedSignatures(signatures, definitions); //signatures without new definitions
    updateDefinitionSignatures(signatures, definitions);

    return new SuccessResponse(true, "Code saved successfully!");
  }

  private SuccessResponse forceSetUcdl(MultipartFile file) {
    try {
      String ucdl = new String(file.getBytes());
      ucdlRepository.setUcdl(ucdl);
    } catch (IOException e) {
      return new SuccessResponse(false, e.getMessage());
    }

    try {
      HashMap<String, ConstraintDefinitionDto> definitions = ucdlRepository.getConstraints();

      List<ConstraintSignature> signatures = constraintSignatureRepository.findAll();

      for (int i = 0; i < signatures.size(); i++) {
        ConstraintSignature signature = signatures.get(i);
        ConstraintDefinitionDto definition = definitions.get(signature.getName());
        if (signatureChanged(signature, definition)) {
          constraintSignatureRepository.delete(signature);
          signatures.remove(i--);
        }
      }

      updateDefinitionSignatures(signatures, definitions);

      return new SuccessResponse(true,
          "Saved file forcefully and deleted inconsistent constraint instances!");
    } catch (ParseException | IOException e) {
      return new SuccessResponse(true, "Saved file with invalid code forcefully!");
    }
  }

  private void updateDefinitionSignatures(List<ConstraintSignature> signatures,
                                          HashMap<String, ConstraintDefinitionDto> definitions) {
    //signatures with potentially new definition
    for (ConstraintSignature signature : signatures) {
      updateSignature(signature, definitions.remove(signature.getName()));
    }

    //new definitions without existing signature
    saveDefinitionSignatures(definitions);
  }

  private void updateSignature(ConstraintSignature signature, ConstraintDefinitionDto definition) {
    if (definition == null || signature == null || !definition.name().equals(signature.getName())) {
      throw new IllegalStateException();
    }

    signature.setDescription(definition.description());
    signature.setDefaultType(definition.defaultType());


    List<ConstraintParameter> newParameters = new ArrayList<>();
    for (Map.Entry<String, ResourceType> parameterEntry : definition.parameters()
        .sequencedEntrySet()) {
      if (parameterEntry.getKey().equals("this")) {
        continue;
      }
      ConstraintParameter parameter = new ConstraintParameter();
      parameter.setParameterName(parameterEntry.getKey());
      parameter.setParameterType(parameterEntry.getValue());
      newParameters.add(parameter);
    }

    //updating existing parameters
    for (int i = 0; i < signature.getParameters().size() && i < newParameters.size(); i++) {
      ConstraintParameter oldParameter = signature.getParameters().get(i);
      ConstraintParameter newParameter = newParameters.get(i);
      oldParameter.setParameterName(newParameter.getParameterName());
      oldParameter.setParameterType(newParameter.getParameterType());
    }

    //deleting old parameters
    for (int i = newParameters.size(); i < signature.getParameters().size(); ) {
      signature.getParameters().remove(i);
    }

    //adding new parameters
    for (int i = signature.getParameters().size(); i < newParameters.size(); i++) {
      signature.getParameters().add(newParameters.get(i));
    }

    this.constraintSignatureRepository.save(signature);
  }

  private void saveDefinitionSignatures(HashMap<String, ConstraintDefinitionDto> definitions) {
    for (ConstraintDefinitionDto definition : definitions.values()) {
      saveDefinitionSignature(definition);
    }
  }

  private void saveDefinitionSignature(ConstraintDefinitionDto definition) {

    List<ConstraintParameter> parameters = new ArrayList<>();
    for (Map.Entry<String, ResourceType> parameterEntry : definition.parameters()
        .sequencedEntrySet()) {
      if (parameterEntry.getKey().equals("this")) {
        continue;
      }
      ConstraintParameter parameter = new ConstraintParameter();
      parameter.setParameterName(parameterEntry.getKey());
      parameter.setParameterType(parameterEntry.getValue());
      parameters.add(parameter);
    }

    ConstraintSignature signature = new ConstraintSignature();
    signature.setName(definition.name());
    signature.setDescription(definition.description());
    signature.setDefaultType(definition.defaultType());
    signature.setParameters(parameters);

    this.constraintSignatureRepository.save(signature);
  }

  private void removeDeletedSignatures(List<ConstraintSignature> signatures,
                                       HashMap<String, ConstraintDefinitionDto> definitions) {
    for (int i = 0; i < signatures.size(); i++) {
      ConstraintSignature signature = signatures.get(i);
      if (definitions.get(signature.getName()) == null) {
        constraintSignatureRepository.delete(signature);
        signatures.remove(i--);
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

    if (definitionParameters.remove() != ResourceType.TIMETABLE) { //removing "this"-parameter
      throw new IllegalStateException();
    }

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
