package de.uftos.repositories.solver.timefold.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.UcdlToken;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.dto.ucdl.ast.ControlSequenceDto;
import de.uftos.dto.ucdl.ast.OperatorDto;
import de.uftos.dto.ucdl.ast.QuantifierDto;
import de.uftos.dto.ucdl.ast.SetDto;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentTimefoldInstance;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:VariableDeclarationUsageDistance"})
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConstraintDefinitionFactoryTest {
  @Test
  void getConstraintDefinition_True() {
    AbstractSyntaxTreeDto root = new ValueDto<>(UcdlToken.BOOL_VALUE, true);

    ConstraintDefinitionDto dto =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            new LinkedHashMap<>(), root);

    ConstraintDefinitionTimefoldInstance definition =
        ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());
  }

  @Test
  void getConstraintDefinition_False() {
    AbstractSyntaxTreeDto root = new ValueDto<>(UcdlToken.BOOL_VALUE, false);

    ConstraintDefinitionDto dto =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            new LinkedHashMap<>(), root);

    ConstraintDefinitionTimefoldInstance definition =
        ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());
  }

  @Test
  void getConstraintDefinition_Implies() {
    AbstractSyntaxTreeDto trueAst = new ValueDto<>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAst = new ValueDto<>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.IMPLIES, List.of(trueAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(trueAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(falseAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(falseAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(finalDto));


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(falseAst, falseAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(finalDto2));
  }

  @Test
  void getConstraintDefinition_Or() {
    AbstractSyntaxTreeDto trueAst = new ValueDto<>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAst = new ValueDto<>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.OR, List.of(trueAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(trueAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(falseAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(falseAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(falseAst, falseAst, falseAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(falseAst, falseAst, trueAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());
  }

  @Test
  void getConstraintDefinition_And() {
    AbstractSyntaxTreeDto trueAst = new ValueDto<>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAst = new ValueDto<>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.AND, List.of(trueAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(trueAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(falseAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(falseAst, falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(trueAst, trueAst, trueAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(trueAst, trueAst, falseAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());
  }

  @Test
  void getConstraintDefinition_Not() {
    AbstractSyntaxTreeDto trueAst = new ValueDto<>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAst = new ValueDto<>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.NOT, List.of(trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.NOT, List.of(falseAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.NOT, List.of(falseAst, trueAst));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(finalDto));
  }

  @Test
  void getConstraintDefinition_ForAll() {
    AbstractSyntaxTreeDto trueAst = new ValueDto<>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAst = new ValueDto<>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    StudentTimefoldInstance student = new StudentTimefoldInstance("student");

    AbstractSyntaxTreeDto variableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "test");
    AbstractSyntaxTreeDto setName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, student.getId());
    AbstractSyntaxTreeDto attribute = new ValueDto<>(UcdlToken.ATTRIBUTE, "studentGroups");
    AbstractSyntaxTreeDto set =
        new SetDto(UcdlToken.RESOURCE_SET, ResourceType.STUDENT_GROUP, setName, List.of(attribute));
    AbstractSyntaxTreeDto ofAst = new OperatorDto(UcdlToken.OF, List.of(variableName, set));

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put(student.getId(), ResourceType.STUDENT);
    List<ResourceTimefoldInstance> arguments = new ArrayList<>(List.of(student));


    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAst, trueAst);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAst, falseAst);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments)); //there are no elements yet
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());

    student.getStudentGroupList().add(null);
    student.getStudentGroupList().add(null);

    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAst, trueAst);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAst, falseAst);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    ofAst = new OperatorDto(UcdlToken.OF, List.of(trueAst, trueAst, trueAst));
    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAst, falseAst);
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));

  }

  @Test
  void getConstraintDefinition_Exists() {
    AbstractSyntaxTreeDto trueAst = new ValueDto<>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAst = new ValueDto<>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    StudentTimefoldInstance student = new StudentTimefoldInstance("student");

    AbstractSyntaxTreeDto variableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "test");
    AbstractSyntaxTreeDto setName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, student.getId());
    AbstractSyntaxTreeDto attribute = new ValueDto<>(UcdlToken.ATTRIBUTE, "studentGroups");
    AbstractSyntaxTreeDto set =
        new SetDto(UcdlToken.RESOURCE_SET, ResourceType.STUDENT_GROUP, setName, List.of(attribute));
    AbstractSyntaxTreeDto ofAst = new OperatorDto(UcdlToken.OF, List.of(variableName, set));

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put(student.getId(), ResourceType.STUDENT);
    List<ResourceTimefoldInstance> arguments = new ArrayList<>(List.of(student));


    root = new QuantifierDto(UcdlToken.EXISTS, ofAst, trueAst);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments)); //there are no elements yet
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new QuantifierDto(UcdlToken.EXISTS, ofAst, falseAst);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());

    student.getStudentGroupList().add(null);
    student.getStudentGroupList().add(null);

    root = new QuantifierDto(UcdlToken.EXISTS, ofAst, trueAst);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new QuantifierDto(UcdlToken.EXISTS, ofAst, falseAst);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    ofAst = new OperatorDto(UcdlToken.OF, List.of(trueAst, trueAst, trueAst));
    root = new QuantifierDto(UcdlToken.EXISTS, ofAst, falseAst);
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));
  }

  @Test
  void getConstraintDefinition_IsEmpty() {
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    StudentTimefoldInstance student = new StudentTimefoldInstance("student");

    AbstractSyntaxTreeDto setName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, student.getId());
    AbstractSyntaxTreeDto attribute = new ValueDto<>(UcdlToken.ATTRIBUTE, "studentGroups");
    AbstractSyntaxTreeDto set =
        new SetDto(UcdlToken.RESOURCE_SET, ResourceType.STUDENT_GROUP, setName, List.of(attribute));

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put(student.getId(), ResourceType.STUDENT);
    List<ResourceTimefoldInstance> arguments = new ArrayList<>(List.of(student));


    root = new OperatorDto(UcdlToken.IS_EMPTY, List.of(set));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    student.getStudentGroupList().add(null);
    student.getStudentGroupList().add(null);


    root = new OperatorDto(UcdlToken.IS_EMPTY, List.of(set));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IS_EMPTY, List.of(set, set));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));
  }

  @Test
  void getConstraintDefinition_In() {
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    AbstractSyntaxTreeDto numberSet = new ValueDto<>(UcdlToken.NUMBER_SET, new Integer[] {1, 2, 3});


    root = new OperatorDto(UcdlToken.IN, List.of(new ValueDto<>(UcdlToken.NUMBER, 1), numberSet));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IN, List.of(new ValueDto<>(UcdlToken.NUMBER, 5), numberSet));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IN, List.of(numberSet));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));


    root = new OperatorDto(UcdlToken.IN, List.of(numberSet, numberSet, numberSet));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2));
  }

  @Test
  void getConstraintDefinition_Equals() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.EQUALS, List.of(number1, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.EQUALS, List.of(number1, number2));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.EQUALS, List.of(number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));


    root = new OperatorDto(UcdlToken.EQUALS, List.of(number1, number1, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2));
  }

  @Test
  void getConstraintDefinition_NotEquals() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.NOT_EQUALS, List.of(number1, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.NOT_EQUALS, List.of(number1, number2));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.NOT_EQUALS, List.of(number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));


    root = new OperatorDto(UcdlToken.NOT_EQUALS, List.of(number1, number1, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2));
  }

  @Test
  void getConstraintDefinition_smaller() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.SMALLER, List.of(number1, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.SMALLER, List.of(number1, number2));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.SMALLER, List.of(number2, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.SMALLER, List.of(number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));


    root = new OperatorDto(UcdlToken.SMALLER, List.of(number1, number1, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2));
  }

  @Test
  void getConstraintDefinition_SmallerEquals() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.SMALLER_EQUALS, List.of(number1, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.SMALLER_EQUALS, List.of(number1, number2));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.SMALLER_EQUALS, List.of(number2, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.SMALLER_EQUALS, List.of(number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));


    root = new OperatorDto(UcdlToken.SMALLER_EQUALS, List.of(number1, number1, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2));
  }

  @Test
  void getConstraintDefinition_Greater() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.GREATER, List.of(number1, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.GREATER, List.of(number1, number2));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.GREATER, List.of(number2, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.GREATER, List.of(number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));


    root = new OperatorDto(UcdlToken.GREATER, List.of(number1, number1, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2));
  }

  @Test
  void getConstraintDefinition_GreaterEquals() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.GREATER_EQUALS, List.of(number1, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.GREATER_EQUALS, List.of(number1, number2));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.GREATER_EQUALS, List.of(number2, number1));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.GREATER_EQUALS, List.of(number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));


    root = new OperatorDto(UcdlToken.GREATER_EQUALS, List.of(number1, number1, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2));
  }

  @Test
  void getConstraintDefinition_CodeBlock() {
    AbstractSyntaxTreeDto trueAst = new ValueDto<>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAst = new ValueDto<>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto returnTrue = new ValueDto<>(UcdlToken.RETURN, true);
    AbstractSyntaxTreeDto returnFalse = new ValueDto<>(UcdlToken.RETURN, false);
    AbstractSyntaxTreeDto ifTrueTrue =
        new ControlSequenceDto(UcdlToken.IF, trueAst, List.of(returnTrue), true);
    AbstractSyntaxTreeDto ifTrueFalse =
        new ControlSequenceDto(UcdlToken.IF, trueAst, List.of(returnFalse), false);
    AbstractSyntaxTreeDto ifFalse =
        new ControlSequenceDto(UcdlToken.IF, falseAst, List.of(returnTrue), true);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;
    List<AbstractSyntaxTreeDto> params;


    params = new ArrayList<>();
    params.add(returnTrue);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(ifTrueTrue);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(ifTrueFalse);
    params.add(returnTrue);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(ifTrueTrue);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(ifFalse);
    params.add(returnTrue);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(ifFalse);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        null, root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));


    params = new ArrayList<>();
    params.add(trueAst);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        null, root);

    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2));

  }

  //todo: implement tests for "if", "for", elements, sets, size, filters and attributes

}
