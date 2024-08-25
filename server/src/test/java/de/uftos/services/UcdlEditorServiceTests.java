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


  @BeforeEach
  void setup() throws IOException, ParseException {
    when(ucdlRepository.getConstraintsFromString(invalidUcdlCode)).thenThrow(ParseException.class);
    when(ucdlRepository.getUcdl()).thenReturn(content);
    when(ucdlRepository.getDefaultUcdl()).thenReturn(content);
    when(signatureMock.getInstances()).thenReturn(List.of());
    when(signatureMock.getName()).thenReturn("newConstraint");
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
    MultipartFile file = new MockMultipartFile("test file", "".getBytes());
    ParsingResponse response = ucdlEditorService.setUcdl(file, true);
    assertEquals(successMessageForceSet, response.message());
  }

  @Test
  void setUcdlEmptyNoForce() {
    MultipartFile file = new MockMultipartFile("test file", "".getBytes());
    ParsingResponse response = ucdlEditorService.setUcdl(file, false);
    assertEquals(successMessageNoForceSet, response.message());
  }

  @Test
  void setUcdlOneConstraintForce() throws ParseException, IOException {
    AbstractSyntaxTreeDto testAst = () -> null;
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put("testParam", ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("newConstraint",
        "Ein neuer Constraint, der immer zu false auswertet.", RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put("newConstraint", definition);

    ConstraintParameter testParameter = new ConstraintParameter();
    testParameter.setParameterName("testParam");
    testParameter.setParameterType(ResourceType.TEACHER);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    MultipartFile file = new MockMultipartFile("test file", """
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
    ParsingResponse response = ucdlEditorService.setUcdl(file, true);
    assertEquals(successMessageForceSet, response.message());
  }

  @Test
  void setUcdlInconsistentParameterAmountsForce() throws ParseException, IOException {
    AbstractSyntaxTreeDto testAst = () -> null;
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put("testParam", ResourceType.TEACHER);
    //the signature contains no parameters, the definition does
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("newConstraint",
        "Ein neuer Constraint, der immer zu false auswertet.", RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put("newConstraint", definition);

    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    MultipartFile file = new MockMultipartFile("test file", """
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
    ParsingResponse response = ucdlEditorService.setUcdl(file, true);
    assertEquals(successMessageForceSet, response.message());
  }

  @Test
  void setUcdlInconsistentParamTypesForce() throws ParseException, IOException {
    AbstractSyntaxTreeDto testAst = () -> null;
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put("testParam", ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("newConstraint",
        "Ein neuer Constraint, der immer zu false auswertet.", RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put("newConstraint", definition);

    ConstraintParameter testParameter = new ConstraintParameter();
    testParameter.setParameterName("testParam");
    testParameter.setParameterType(ResourceType.STUDENT);
    //parameter types do not match between signature and definition
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    MultipartFile file = new MockMultipartFile("test file", """
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
    ParsingResponse response = ucdlEditorService.setUcdl(file, true);
    assertEquals(successMessageForceSet, response.message());
  }


  @Test
  void setUcdlWrongThisParamForce() throws ParseException, IOException {
    AbstractSyntaxTreeDto testAst = () -> null;
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TEACHER); //this parameter should have type timetable
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("newConstraint",
        "Ein neuer Constraint, der immer zu false auswertet.", RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put("newConstraint", definition);
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    MultipartFile file = new MockMultipartFile("test file", """
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
    assertThrows(IllegalStateException.class, () -> ucdlEditorService.setUcdl(file, true));
  }

  @Test
  void setUcdlInconsistentSignatureNameForce() throws ParseException, IOException {
    AbstractSyntaxTreeDto testAst = () -> null;
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("non-existent name",
        "Ein neuer Constraint, der immer zu false auswertet.", RewardPenalize.HARD_PENALIZE,
        parameters, testAst);     //definition name does not match signature name
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put("newConstraint", definition);
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    MultipartFile file = new MockMultipartFile("test file", """
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
    assertThrows(IllegalArgumentException.class, () -> ucdlEditorService.setUcdl(file, true));
  }

  @Test
  void setUcdlOneConstraintNoForce() throws ParseException, IOException {
    AbstractSyntaxTreeDto testAst = () -> null;
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put("testParam", ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("newConstraint",
        "Ein neuer Constraint, der immer zu false auswertet.", RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put("newConstraint", definition);

    ConstraintParameter testParameter = new ConstraintParameter();
    testParameter.setParameterName("testParam");
    testParameter.setParameterType(ResourceType.TEACHER);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    when(ucdlRepository.getConstraints()).thenReturn(getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    MultipartFile file = new MockMultipartFile("test file", """
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
    ParsingResponse response = ucdlEditorService.setUcdl(file, false);
    assertEquals(successMessageNoForceSet, response.message());
  }

  @Test
  void setUcdlSignatureChangedNoForce() throws ParseException, IOException {
    String testDefinition = """
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
    String signatureChangedNoForceMessage =
        "Signaturen von Constraints haben sich geändert! "
            + "Constraint Instanzen werden gelöscht, wenn der Code gespeichert wird!";
    AbstractSyntaxTreeDto testAst = () -> null;
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put("testParam", ResourceType.TEACHER);
    ConstraintDefinitionDto definition = new ConstraintDefinitionDto("newConstraint",
        "Ein neuer Constraint, der immer zu false auswertet.", RewardPenalize.HARD_PENALIZE,
        parameters, testAst);
    HashMap<String, ConstraintDefinitionDto> getConstraintsResponse = new HashMap<>();
    getConstraintsResponse.put("newConstraint", definition);

    ConstraintParameter testParameter = new ConstraintParameter();
    testParameter.setParameterName("testParam");
    testParameter.setParameterType(ResourceType.STUDENT);
    when(signatureMock.getParameters()).thenReturn(List.of(testParameter));
    ConstraintInstance instance = new ConstraintInstance();
    when(signatureMock.getInstances()).thenReturn(List.of(instance));
    when(ucdlRepository.getConstraintsFromString(testDefinition)).thenReturn(
        getConstraintsResponse);
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(signatureMock));

    MultipartFile file = new MockMultipartFile("test file", """
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
    ParsingResponse response = ucdlEditorService.setUcdl(file, false);
    assertEquals(signatureChangedNoForceMessage, response.message());
  }

  @Test
  void setUcdlInvalidConstraintNoForce() {
    MultipartFile file = new MockMultipartFile("test file", invalidUcdlCode.getBytes());
    ParsingResponse response = ucdlEditorService.setUcdl(file, false);
    assertFalse(response.success());
  }

  @Test
  void setUcdlInvalidConstraintForce() throws ParseException, IOException {
    when(ucdlRepository.getConstraints()).thenThrow(ParseException.class);
    String forceSetWithInvalidCodeMessage = "Änderungen mit invalidem Code gespeichert!";

    MultipartFile file = new MockMultipartFile("test file", invalidUcdlCode.getBytes());
    ParsingResponse response = ucdlEditorService.setUcdl(file, true);
    assertEquals(response.message(), forceSetWithInvalidCodeMessage);
  }


  @Test
  void validateFile() {
    MultipartFile file = new MockMultipartFile("test file", "test content".getBytes());
    assertDoesNotThrow(() -> ucdlEditorService.validate(file));
  }

}
