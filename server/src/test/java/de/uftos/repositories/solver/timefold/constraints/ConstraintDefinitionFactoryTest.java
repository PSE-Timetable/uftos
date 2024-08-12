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

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConstraintDefinitionFactoryTest {
  @Test
  void getConstraintDefinition_TRUE() {
    AbstractSyntaxTreeDto root = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, true);

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
  void getConstraintDefinition_FALSE() {
    AbstractSyntaxTreeDto root = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, false);

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
  void getConstraintDefinition_IMPLIES() {
    AbstractSyntaxTreeDto trueAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.IMPLIES, List.of(trueAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(trueAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(falseAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(falseAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(finalDto));


    root = new OperatorDto(UcdlToken.IMPLIES, List.of(falseAST, falseAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(finalDto2));
  }

  @Test
  void getConstraintDefinition_OR() {
    AbstractSyntaxTreeDto trueAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.OR, List.of(trueAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(trueAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(falseAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(falseAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(falseAST, falseAST, falseAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.OR, List.of(falseAST, falseAST, trueAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());
  }

  @Test
  void getConstraintDefinition_AND() {
    AbstractSyntaxTreeDto trueAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.AND, List.of(trueAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(trueAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(falseAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(falseAST, falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(trueAST, trueAST, trueAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.AND, List.of(trueAST, trueAST, falseAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());
  }

  @Test
  void getConstraintDefinition_NOT() {
    AbstractSyntaxTreeDto trueAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    root = new OperatorDto(UcdlToken.NOT, List.of(trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.NOT, List.of(falseAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new OperatorDto(UcdlToken.NOT, List.of(falseAST, trueAST));

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(finalDto));
  }

  @Test
  void getConstraintDefinition_FOR_ALL() {
    AbstractSyntaxTreeDto trueAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    StudentTimefoldInstance student = new StudentTimefoldInstance("student");

    AbstractSyntaxTreeDto variableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "test");
    AbstractSyntaxTreeDto setName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, student.getId());
    AbstractSyntaxTreeDto attribute = new ValueDto<>(UcdlToken.ATTRIBUTE, "studentGroups");
    AbstractSyntaxTreeDto set =
        new SetDto(UcdlToken.RESOURCE_SET, ResourceType.STUDENT_GROUP, setName, List.of(attribute));
    AbstractSyntaxTreeDto ofAST = new OperatorDto(UcdlToken.OF, List.of(variableName, set));

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put(student.getId(), ResourceType.STUDENT);
    List<ResourceTimefoldInstance> arguments = new ArrayList<>(List.of(student));


    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAST, trueAST);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAST, falseAST);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments)); //there are no elements yet
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());

    student.getStudentGroupList().add(null);
    student.getStudentGroupList().add(null);

    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAST, trueAST);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAST, falseAST);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    ofAST = new OperatorDto(UcdlToken.OF, List.of(trueAST, trueAST, trueAST));
    root = new QuantifierDto(UcdlToken.FOR_ALL, ofAST, falseAST);
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));

  }

  @Test
  void getConstraintDefinition_EXISTS() {
    AbstractSyntaxTreeDto trueAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto falseAST = new ValueDto<Boolean>(UcdlToken.BOOL_VALUE, false);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;

    StudentTimefoldInstance student = new StudentTimefoldInstance("student");

    AbstractSyntaxTreeDto variableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "test");
    AbstractSyntaxTreeDto setName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, student.getId());
    AbstractSyntaxTreeDto attribute = new ValueDto<>(UcdlToken.ATTRIBUTE, "studentGroups");
    AbstractSyntaxTreeDto set =
        new SetDto(UcdlToken.RESOURCE_SET, ResourceType.STUDENT_GROUP, setName, List.of(attribute));
    AbstractSyntaxTreeDto ofAST = new OperatorDto(UcdlToken.OF, List.of(variableName, set));

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    parameters.put(student.getId(), ResourceType.STUDENT);
    List<ResourceTimefoldInstance> arguments = new ArrayList<>(List.of(student));


    root = new QuantifierDto(UcdlToken.EXISTS, ofAST, trueAST);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments)); //there are no elements yet
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new QuantifierDto(UcdlToken.EXISTS, ofAST, falseAST);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());

    student.getStudentGroupList().add(null);
    student.getStudentGroupList().add(null);

    root = new QuantifierDto(UcdlToken.EXISTS, ofAST, trueAST);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    root = new QuantifierDto(UcdlToken.EXISTS, ofAST, falseAST);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    ofAST = new OperatorDto(UcdlToken.OF, List.of(trueAST, trueAST, trueAST));
    root = new QuantifierDto(UcdlToken.EXISTS, ofAST, falseAST);
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        parameters, root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto));
  }
}
