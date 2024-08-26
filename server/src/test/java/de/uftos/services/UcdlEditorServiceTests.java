package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.ParsingResponse;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintParameter;
import de.uftos.entities.ConstraintSignature;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.ucdl.UcdlRepository;
import de.uftos.repositories.ucdl.parser.PredefinedConstraint;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UcdlEditorServiceTests {
  @Mock
  private UcdlRepository ucdlRepository;

  @Mock
  private ConstraintSignatureRepository constraintSignatureRepository;

  @Mock
  private ConstraintSignature signatureMock;

  @InjectMocks
  private UcdlEditorService ucdlEditorService;

  private final String content =
      Arrays.stream(PredefinedConstraint.values()).map(PredefinedConstraint::getCode)
          .collect(Collectors.joining("\n"));

  private final String successMessageForceSet = "Änderungen gespeichert und inkonsistente"
      + " Constraint Instanzen gelöscht!";
  private final String successMessageNoForceSet = "Code erfolgreich gespeichert!";

  private final MultipartFile emptyFile = new MockMultipartFile("test file", "".getBytes());

  private final String invalidUcdlCode = """
      newConstraint:
        description: "Ein neuer Constraint, der immer zu false auswertet."
        default_type: HARD_PENALIZE
        parameter:
        definition: >-
          if (false) {
            totally invalid code skhgfdsk
          }
          return kasjhdfkja false
      """;

  private final MultipartFile validUcdlFile = new MockMultipartFile("test file", """
      newConstraint:
        description: "Ein neuer Constraint, der immer zu false auswertet."
        default_type: HARD_PENALIZE
        parameter:
        definition: >-
          if (false) {
            return true
          }
          return false
      """.getBytes());


  private final MultipartFile invalidUcdlFile =
      new MockMultipartFile("test file", invalidUcdlCode.getBytes());

  private final String testDefinition = """
      newConstraint:
        description: "Ein neuer Constraint, der immer zu false auswertet."
        default_type: HARD_PENALIZE
        parameter:
        definition: >-
          if (false) {
            return true
          }
          return false
      """;

  private final String constraintName = "newConstraint";

  private final String constraintDescription =
      "Ein neuer Constraint, der immer zu false auswertet.";

  private final ConstraintParameter testParameter = new ConstraintParameter();

  private final String testParameterName = "testParam";

  private final AbstractSyntaxTreeDto testAst = () -> null;


  @BeforeEach
  void setup() throws IOException, ParseException {
    when(ucdlRepository.getConstraintsFromString(invalidUcdlCode)).thenThrow(ParseException.class);
    when(ucdlRepository.getUcdl()).thenReturn(content);
    when(ucdlRepository.getDefaultUcdl()).thenReturn(content);
    when(signatureMock.getInstances()).thenReturn(List.of());
    when(signatureMock.getName()).thenReturn(constraintName);
    when(signatureMock.getParameters()).thenReturn(List.of());
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));
  }

  @Test
  void getUcdl() throws IOException {
    assertDoesNotThrow(() -> ucdlEditorService.getUcdl());
    Resource result = ucdlEditorService.getUcdl();
    assertEquals(content, result.getContentAsString(StandardCharsets.UTF_8));
  }

  @Test
  void getDefaultUcdl() throws IOException {
    Resource result = ucdlEditorService.getDefaultUcdl();
    assertEquals(content, result.getContentAsString(StandardCharsets.UTF_8));
  }

  @Test
  void setUcdlEmptyForce() {
    ParsingResponse response = ucdlEditorService.setUcdl(emptyFile, true);
    assertEquals(successMessageForceSet, response.message());
  }

  @Test
  void setUcdlEmptyNoForce() {
    ParsingResponse response = ucdlEditorService.setUcdl(emptyFile, false);
    assertEquals(successMessageNoForceSet, response.message());
  }

  @Test
  void setUcdlOneConstraintForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put(testParameterName, ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto(constraintName,
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);

    testParameter.setParameterName(testParameterName);
    testParameter.setParameterType(ResourceType.TEACHER);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    ParsingResponse response = ucdlEditorService.setUcdl(validUcdlFile, true);
    assertEquals(successMessageForceSet, response.message());
  }

  @Test
  void setUcdlInconsistentParameterAmountsForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put(testParameterName, ResourceType.TEACHER);
    //the signature contains no parameters, the definition does
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto(constraintName,
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);

    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));


    ParsingResponse response = ucdlEditorService.setUcdl(validUcdlFile, true);
    assertEquals(successMessageForceSet, response.message());
  }

  @Test
  void setUcdlInconsistentParamTypesForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put(testParameterName, ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto(constraintName,
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);

    testParameter.setParameterName(testParameterName);
    testParameter.setParameterType(ResourceType.STUDENT);
    //parameter types do not match between signature and definition
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    ParsingResponse response = ucdlEditorService.setUcdl(validUcdlFile, true);
    assertEquals(successMessageForceSet, response.message());
  }


  @Test
  void setUcdlWrongThisParamForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TEACHER); //this parameter should have type timetable
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto(constraintName,
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    assertThrows(IllegalStateException.class, () -> ucdlEditorService.setUcdl(validUcdlFile, true));
  }

  @Test
  void setUcdlInconsistentSignatureNameForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("non-existent name",
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);     //definition name does not match signature name

    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    assertThrows(IllegalArgumentException.class,
        () -> ucdlEditorService.setUcdl(validUcdlFile, true));
  }

  @Test
  void setUcdlOneConstraintNoForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put(testParameterName, ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto(constraintName,
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);

    testParameter.setParameterName(testParameterName);
    testParameter.setParameterType(ResourceType.TEACHER);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    ParsingResponse response = ucdlEditorService.setUcdl(validUcdlFile, false);
    assertEquals(successMessageNoForceSet, response.message());
  }

  @Test
  void setUcdlSignatureChangedNoForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put(testParameterName, ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto(constraintName,
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);

    testParameter.setParameterName(testParameterName);
    testParameter.setParameterType(ResourceType.STUDENT);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    ConstraintInstance instance = new ConstraintInstance();
    when(signatureMock.getInstances()).thenReturn(List.of(instance));
    when(ucdlRepository.getConstraintsFromString(testDefinition)).thenReturn(
        getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    ParsingResponse response = ucdlEditorService.setUcdl(validUcdlFile, false);
    assertEquals("Signaturen von Constraints haben sich geändert! "
            + "Constraint Instanzen werden gelöscht, wenn der Code gespeichert wird!",
        response.message());
  }

  @Test
  void setUcdlSignatureChangedNoInstancesNoForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put(testParameterName, ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("different name",
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    //name in definition dto does not match signature

    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);

    testParameter.setParameterName(testParameterName);
    testParameter.setParameterType(ResourceType.STUDENT);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    when(ucdlRepository.getConstraintsFromString(testDefinition)).thenReturn(
        getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    assertThrows(IllegalStateException.class,
        () -> ucdlEditorService.setUcdl(validUcdlFile, false));
  }

  @Test
  void setUcdlNewParameterNoInstancesNoForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put(testParameterName, ResourceType.TEACHER);
    parameters.put("newParam", ResourceType.STUDENT);
    //third parameter is not present in signature

    ConstraintDefinitionDto definition = new ConstraintDefinitionDto(constraintName,
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);

    testParameter.setParameterName(testParameterName);
    testParameter.setParameterType(ResourceType.STUDENT);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    when(ucdlRepository.getConstraintsFromString(testDefinition)).thenReturn(
        getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    ParsingResponse response = ucdlEditorService.setUcdl(validUcdlFile, false);
    assertEquals(response.message(), successMessageNoForceSet);
  }

  @Test
  void setUcdlDeletedParameterNoInstancesNoForce() throws ParseException, IOException {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    //definition is missing a parameter from the signature

    ConstraintDefinitionDto definition = new ConstraintDefinitionDto(constraintName,
        constraintDescription, RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put(constraintName, definition);

    testParameter.setParameterName(testParameterName);
    testParameter.setParameterType(ResourceType.STUDENT);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter))
        .thenReturn(List.of(testParameter))
        .thenReturn(List.of(testParameter))
        .thenReturn(List.of());
    //on fourth call, the parameter has been removed from the list and is no longer returned

    when(ucdlRepository.getConstraintsFromString(testDefinition)).thenReturn(
        getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    ParsingResponse response = ucdlEditorService.setUcdl(validUcdlFile, false);
    assertEquals(response.message(), successMessageNoForceSet);
  }

  @Test
  void setUcdlInvalidConstraintNoForce() {
    ParsingResponse response = ucdlEditorService.setUcdl(invalidUcdlFile, false);
    assertFalse(response.success());
  }

  @Test
  void setUcdlInvalidConstraintForce() throws ParseException, IOException {
    when(ucdlRepository.getConstraints()).thenThrow(ParseException.class);

    ParsingResponse response = ucdlEditorService.setUcdl(invalidUcdlFile, true);
    assertEquals("Änderungen mit invalidem Code gespeichert!", response.message());
  }

  @Test
  void validateFile() {
    MultipartFile file = new MockMultipartFile("test file", "test content".getBytes());
    assertDoesNotThrow(() -> ucdlEditorService.validate(file));
  }

}
