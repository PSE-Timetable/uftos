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
import de.uftos.dto.ucdl.ast.ElementDto;
import de.uftos.dto.ucdl.ast.OperatorDto;
import de.uftos.dto.ucdl.ast.QuantifierDto;
import de.uftos.dto.ucdl.ast.SetDto;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.repositories.solver.timefold.domain.GradeTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.LessonTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.RoomTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentGroupTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.SubjectTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TagTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TeacherTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimeslotTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimetableSolutionTimefoldInstance;
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
  void getConstraintDefinitionTrue() {
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
  void getConstraintDefinitionFalse() {
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
  void getConstraintDefinitionImplies() {
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
  void getConstraintDefinitionOr() {
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
  void getConstraintDefinitionAnd() {
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
  void getConstraintDefinitionNot() {
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
  void getConstraintDefinitionForAll() {
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
  void getConstraintDefinitionExists() {
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
  void getConstraintDefinitionsBool() {
    AbstractSyntaxTreeDto root = new ValueDto<>(UcdlToken.NUMBER, 5);

    ConstraintDefinitionDto dto =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            new LinkedHashMap<>(), root);

    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(dto).evaluationFunction()
            .apply(new ArrayList<>()));
  }

  @Test
  void getConstraintDefinitionIsEmpty() {
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
  void getConstraintDefinitionIn() {
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
  void getConstraintDefinitionEquals() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
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


    root = new OperatorDto(UcdlToken.EQUALS, List.of(number1, timetable));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto3 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto3).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));
  }

  @Test
  void getConstraintDefinitionNotEquals() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
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


    root = new OperatorDto(UcdlToken.NOT_EQUALS, List.of(number1, timetable));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto3 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto3).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));
  }

  @Test
  void getConstraintDefinitionSmaller() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
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


    root = new OperatorDto(UcdlToken.SMALLER, List.of(number1, timetable));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto3 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto3).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));


    root = new OperatorDto(UcdlToken.SMALLER, List.of(timetable, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto4 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto4).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));
  }

  @Test
  void getConstraintDefinitionSmallerEquals() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
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


    root = new OperatorDto(UcdlToken.SMALLER_EQUALS, List.of(number1, timetable));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto3 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto3).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));


    root = new OperatorDto(UcdlToken.SMALLER_EQUALS, List.of(timetable, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto4 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto4).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));
  }

  @Test
  void getConstraintDefinitionGreater() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
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


    root = new OperatorDto(UcdlToken.GREATER, List.of(number1, timetable));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto3 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto3).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));


    root = new OperatorDto(UcdlToken.GREATER, List.of(timetable, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto4 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto4).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));
  }

  @Test
  void getConstraintDefinitionGreaterEquals() {
    AbstractSyntaxTreeDto number1 = new ValueDto<>(UcdlToken.NUMBER, 1);
    AbstractSyntaxTreeDto number2 = new ValueDto<>(UcdlToken.NUMBER, 2);
    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
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


    root = new OperatorDto(UcdlToken.GREATER_EQUALS, List.of(number1, timetable));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto3 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto3).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));


    root = new OperatorDto(UcdlToken.GREATER_EQUALS, List.of(timetable, number1));
    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);
    ConstraintDefinitionDto finalDto4 = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
                finalDto4).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));
  }

  @Test
  void getConstraintDefinitionCodeBlock() {
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
    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
    AbstractSyntaxTreeDto reference = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "x");
    AbstractSyntaxTreeDto ofAst = new OperatorDto(UcdlToken.OF, List.of(reference, timetable));
    AbstractSyntaxTreeDto forThis =
        new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(ifTrueTrue), true);
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
    params.add(forThis);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(
        definition.evaluationFunction()
            .apply(new ArrayList<>(List.of(new TimetableSolutionTimefoldInstance()))));
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

  @Test
  void getConstraintDefinitionFor() {
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


    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
    AbstractSyntaxTreeDto reference = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "x");
    AbstractSyntaxTreeDto ofAst = new OperatorDto(UcdlToken.OF, List.of(reference, timetable));
    AbstractSyntaxTreeDto forThis =
        new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(ifTrueTrue), true);


    params = new ArrayList<>();
    params.add(forThis);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(
        definition.evaluationFunction()
            .apply(new ArrayList<>(List.of(new TimetableSolutionTimefoldInstance()))));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    AbstractSyntaxTreeDto students =
        new SetDto(UcdlToken.RESOURCE_SET, ResourceType.STUDENT, timetableName,
            new ArrayList<>(List.of(new ValueDto<>(UcdlToken.ATTRIBUTE, "students"))));
    ofAst = new OperatorDto(UcdlToken.OF, List.of(reference, students));
    AbstractSyntaxTreeDto forStudents =
        new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(ifTrueTrue), true);


    params = new ArrayList<>();
    params.add(forStudents);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(
        definition.evaluationFunction()
            .apply(new ArrayList<>(List.of(new TimetableSolutionTimefoldInstance()))));
    TimetableSolutionTimefoldInstance timetableWithStudent =
        new TimetableSolutionTimefoldInstance();
    timetableWithStudent.getStudents().add(new StudentTimefoldInstance("student"));
    assertTrue(
        definition.evaluationFunction()
            .apply(new ArrayList<>(List.of(timetableWithStudent))));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    AbstractSyntaxTreeDto number =
        new ValueDto<>(UcdlToken.NUMBER, 5);
    ofAst = new OperatorDto(UcdlToken.OF, List.of(reference, number));
    AbstractSyntaxTreeDto forNumber =
        new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(ifTrueTrue), true);


    params = new ArrayList<>();
    params.add(forNumber);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(
        definition.evaluationFunction()
            .apply(new ArrayList<>()));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    AbstractSyntaxTreeDto numberSet =
        new ValueDto<>(UcdlToken.NUMBER_SET, new Integer[] {5});
    ofAst = new OperatorDto(UcdlToken.OF, List.of(reference, numberSet));
    AbstractSyntaxTreeDto forNumberSet =
        new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(ifTrueTrue), true);


    params = new ArrayList<>();
    params.add(forNumberSet);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(
        definition.evaluationFunction()
            .apply(new ArrayList<>()));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    AbstractSyntaxTreeDto forNumberSetMultipleBodyFunctions =
        new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(ifFalse, forNumberSet, ifTrueTrue),
            true);


    params = new ArrayList<>();
    params.add(forNumberSetMultipleBodyFunctions);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(
        definition.evaluationFunction()
            .apply(new ArrayList<>()));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    AbstractSyntaxTreeDto forFalse =
        new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(ifFalse), true);


    params = new ArrayList<>();
    params.add(forFalse);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(
        definition.evaluationFunction()
            .apply(new ArrayList<>()));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    AbstractSyntaxTreeDto ofThrows =
        new OperatorDto(UcdlToken.OF, List.of(trueAst, trueAst, trueAst));
    AbstractSyntaxTreeDto forThrows =
        new ControlSequenceDto(UcdlToken.FOR, ofThrows, List.of(ifFalse), true);

    params = new ArrayList<>();
    params.add(forThrows);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto).evaluationFunction().apply(new ArrayList<>()));


    ofThrows = new OperatorDto(UcdlToken.OF, List.of(reference, trueAst));
    forThrows = new ControlSequenceDto(UcdlToken.FOR, ofThrows, List.of(ifFalse), true);

    params = new ArrayList<>();
    params.add(forThrows);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto2 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto2).evaluationFunction().apply(new ArrayList<>()));


    forThrows = new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(trueAst), true);

    params = new ArrayList<>();
    params.add(forThrows);
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto3 = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(
            finalDto3).evaluationFunction().apply(new ArrayList<>()));

  }

  @Test
  void getConstraintDefinitionIf() {
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
    AbstractSyntaxTreeDto timetableName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto timetable =
        new ElementDto(UcdlToken.ELEMENT, timetableName, new ArrayList<>(), ResourceType.TIMETABLE);
    AbstractSyntaxTreeDto reference = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "x");
    AbstractSyntaxTreeDto ofAst = new OperatorDto(UcdlToken.OF, List.of(reference, timetable));
    AbstractSyntaxTreeDto forThis =
        new ControlSequenceDto(UcdlToken.FOR, ofAst, List.of(ifTrueTrue), true);
    AbstractSyntaxTreeDto root;
    ConstraintDefinitionDto dto;
    ConstraintDefinitionTimefoldInstance definition;
    List<AbstractSyntaxTreeDto> params;


    params = new ArrayList<>();
    params.add(
        new ControlSequenceDto(UcdlToken.IF, trueAst, List.of(ifTrueTrue, ifTrueTrue, ifTrueTrue),
            true));
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(new ControlSequenceDto(UcdlToken.IF, trueAst, List.of(forThis), true));
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertTrue(definition.evaluationFunction()
        .apply(new ArrayList<>(List.of(new TimetableSolutionTimefoldInstance()))));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(
        new ControlSequenceDto(UcdlToken.IF, falseAst, List.of(ifTrueTrue, ifTrueTrue, ifTrueTrue),
            true));
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(new ControlSequenceDto(UcdlToken.IF, falseAst, List.of(forThis), true));
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction()
        .apply(new ArrayList<>(List.of(new TimetableSolutionTimefoldInstance()))));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(
        new ControlSequenceDto(UcdlToken.IF, trueAst, List.of(ifFalse),
            true));
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    definition = ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());


    params = new ArrayList<>();
    params.add(
        new ControlSequenceDto(UcdlToken.IF, trueAst, List.of(trueAst),
            true));
    params.add(returnFalse);
    root = new OperatorDto(UcdlToken.CODEBLOCK, params);

    dto = new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
        new LinkedHashMap<>(), root);

    ConstraintDefinitionDto finalDto = dto;
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(finalDto).evaluationFunction()
            .apply(null));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());
  }

  @Test
  void getConstraintDefinitionAttributes() {
    testValidAttribute("this", "grades", ResourceType.GRADE, true);
    testValidAttribute("this", "lessons", ResourceType.LESSON, true);
    testValidAttribute("this", "rooms", ResourceType.ROOM, true);
    testValidAttribute("this", "subjects", ResourceType.SUBJECT, true);
    testValidAttribute("this", "students", ResourceType.STUDENT, true);
    testValidAttribute("this", "studentGroups", ResourceType.STUDENT_GROUP, true);
    testValidAttribute("this", "tags", ResourceType.TAG, true);
    testValidAttribute("this", "teachers", ResourceType.TEACHER, true);
    testValidAttribute("this", "timeslots", ResourceType.TIMESLOT, true);

    testValidAttribute("grade", "studentGroups", ResourceType.STUDENT_GROUP, true);
    testValidAttribute("grade", "tags", ResourceType.TAG, true);

    testValidAttribute("lesson", "index", ResourceType.NUMBER, false);
    testValidAttribute("lesson", "room", ResourceType.ROOM, false);
    testValidAttribute("lesson", "subject", ResourceType.SUBJECT, false);
    testValidAttribute("lesson", "studentGroup", ResourceType.STUDENT_GROUP, false);
    testValidAttribute("lesson", "teacher", ResourceType.TEACHER, false);
    testValidAttribute("lesson", "timeslot", ResourceType.TIMESLOT, false);

    testValidAttribute("room", "lessons", ResourceType.LESSON, true);
    testValidAttribute("room", "tags", ResourceType.TAG, true);

    testValidAttribute("subject", "lessons", ResourceType.LESSON, true);
    testValidAttribute("subject", "tags", ResourceType.TAG, true);
    testValidAttribute("subject", "teachers", ResourceType.TEACHER, true);

    testValidAttribute("student", "studentGroups", ResourceType.STUDENT_GROUP, true);
    testValidAttribute("student", "tags", ResourceType.TAG, true);

    testValidAttribute("studentGroup", "grade", ResourceType.GRADE, false);
    testValidAttribute("studentGroup", "lessons", ResourceType.LESSON, true);
    testValidAttribute("studentGroup", "students", ResourceType.STUDENT, true);
    testValidAttribute("studentGroup", "tags", ResourceType.TAG, true);

    testValidAttribute("tag", "grades", ResourceType.GRADE, true);
    testValidAttribute("tag", "rooms", ResourceType.ROOM, true);
    testValidAttribute("tag", "subjects", ResourceType.SUBJECT, true);
    testValidAttribute("tag", "students", ResourceType.STUDENT, true);
    testValidAttribute("tag", "studentGroups", ResourceType.STUDENT_GROUP, true);
    testValidAttribute("tag", "teachers", ResourceType.TEACHER, true);
    testValidAttribute("tag", "timeslots", ResourceType.TIMESLOT, true);

    testValidAttribute("teacher", "lessons", ResourceType.LESSON, true);
    testValidAttribute("teacher", "subjects", ResourceType.SUBJECT, true);
    testValidAttribute("teacher", "tags", ResourceType.TAG, true);

    testValidAttribute("timeslot", "day", ResourceType.NUMBER, false);
    testValidAttribute("timeslot", "lessons", ResourceType.LESSON, true);
    testValidAttribute("timeslot", "slot", ResourceType.NUMBER, false);
    testValidAttribute("timeslot", "tags", ResourceType.TAG, true);

    testInvalidAttribute("student", "index", ResourceType.NUMBER);
    testInvalidAttribute("student", "teacher", ResourceType.TEACHER);
    testInvalidAttribute("student", "timeslot", ResourceType.TIMESLOT);
    testInvalidAttribute("student", "room", ResourceType.ROOM);
    testInvalidAttribute("student", "subject", ResourceType.SUBJECT);
    testInvalidAttribute("student", "studentGroup", ResourceType.STUDENT_GROUP);
    testInvalidAttribute("student", "grade", ResourceType.GRADE);
    testInvalidAttribute("student", "day", ResourceType.NUMBER);
    testInvalidAttribute("student", "slot", ResourceType.NUMBER);
    testInvalidAttribute("student", "students", ResourceType.STUDENT);
    testInvalidAttribute("student", "teachers", ResourceType.TEACHER);
    testInvalidAttribute("studentGroup", "studentGroups", ResourceType.STUDENT_GROUP);
    testInvalidAttribute("student", "rooms", ResourceType.ROOM);
    testInvalidAttribute("student", "subjects", ResourceType.SUBJECT);
    testInvalidAttribute("student", "grades", ResourceType.GRADE);
    testInvalidAttribute("student", "timeslots", ResourceType.TIMESLOT);
    testInvalidAttribute("student", "lessons", ResourceType.LESSON);
    testInvalidAttribute("tag", "tags", ResourceType.TAG);
    testInvalidAttribute("student", "test", ResourceType.NUMBER);
  }

  private void testValidAttribute(String setName, String attributeName, ResourceType setType,
                                  boolean isEmpty) {
    List<ResourceTimefoldInstance> arguments = new ArrayList<>();
    arguments.add(new TimetableSolutionTimefoldInstance());
    arguments.add(new GradeTimefoldInstance("grade"));
    arguments.add(new LessonTimefoldInstance("lesson"));
    arguments.add(new RoomTimefoldInstance("room"));
    arguments.add(new SubjectTimefoldInstance("subject"));
    arguments.add(new StudentTimefoldInstance("student"));
    arguments.add(new StudentGroupTimefoldInstance("studentGroup"));
    arguments.add(new TagTimefoldInstance("tag"));
    arguments.add(new TeacherTimefoldInstance("teacher"));
    arguments.add(new TimeslotTimefoldInstance("timeslot"));

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    for (ResourceTimefoldInstance resource : arguments) {
      parameters.put(resource.getId(), resource.getResourceType());
    }

    AbstractSyntaxTreeDto name = new ValueDto<>(UcdlToken.VALUE_REFERENCE, setName);
    AbstractSyntaxTreeDto attribute = new ValueDto<>(UcdlToken.ATTRIBUTE, attributeName);
    AbstractSyntaxTreeDto set =
        new SetDto(UcdlToken.RESOURCE_SET, setType, name, List.of(attribute));

    AbstractSyntaxTreeDto root = new OperatorDto(UcdlToken.IS_EMPTY, List.of(set));
    ConstraintDefinitionDto dto =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            parameters, root);
    ConstraintDefinitionTimefoldInstance definition =
        ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertEquals(isEmpty, definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());
  }

  private void testInvalidAttribute(String setName, String attributeName, ResourceType setType) {
    List<ResourceTimefoldInstance> arguments = new ArrayList<>();
    arguments.add(new TimetableSolutionTimefoldInstance());
    arguments.add(new GradeTimefoldInstance("grade"));
    arguments.add(new LessonTimefoldInstance("lesson"));
    arguments.add(new RoomTimefoldInstance("room"));
    arguments.add(new SubjectTimefoldInstance("subject"));
    arguments.add(new StudentTimefoldInstance("student"));
    arguments.add(new StudentGroupTimefoldInstance("studentGroup"));
    arguments.add(new TagTimefoldInstance("tag"));
    arguments.add(new TeacherTimefoldInstance("teacher"));
    arguments.add(new TimeslotTimefoldInstance("timeslot"));

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    for (ResourceTimefoldInstance resource : arguments) {
      parameters.put(resource.getId(), resource.getResourceType());
    }

    AbstractSyntaxTreeDto name = new ValueDto<>(UcdlToken.VALUE_REFERENCE, setName);
    AbstractSyntaxTreeDto attribute = new ValueDto<>(UcdlToken.ATTRIBUTE, attributeName);
    AbstractSyntaxTreeDto set =
        new SetDto(UcdlToken.RESOURCE_SET, setType, name, List.of(attribute));

    AbstractSyntaxTreeDto root = new OperatorDto(UcdlToken.IS_EMPTY, List.of(set));
    ConstraintDefinitionDto dto =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            parameters, root);
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(dto).evaluationFunction()
            .apply(arguments));
  }

  @Test
  void getConstraintDefinitionElement() {
    List<ResourceTimefoldInstance> arguments = new ArrayList<>();
    arguments.add(new TimetableSolutionTimefoldInstance());
    arguments.add(new GradeTimefoldInstance("grade"));
    arguments.add(new LessonTimefoldInstance("lesson"));
    arguments.add(new RoomTimefoldInstance("room"));
    arguments.add(new SubjectTimefoldInstance("subject"));
    arguments.add(new StudentTimefoldInstance("student"));
    arguments.add(new StudentGroupTimefoldInstance("studentGroup"));
    arguments.add(new TagTimefoldInstance("tag"));
    arguments.add(new TeacherTimefoldInstance("teacher"));
    arguments.add(new TimeslotTimefoldInstance("timeslot"));

    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();
    for (ResourceTimefoldInstance resource : arguments) {
      parameters.put(resource.getId(), resource.getResourceType());
    }

    AbstractSyntaxTreeDto name = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "lesson");
    AbstractSyntaxTreeDto attribute = new ValueDto<>(UcdlToken.ATTRIBUTE, "index");
    AbstractSyntaxTreeDto element =
        new ElementDto(UcdlToken.ELEMENT, name, List.of(attribute), ResourceType.NUMBER);

    AbstractSyntaxTreeDto root = new OperatorDto(UcdlToken.IS_EMPTY, List.of(element));
    ConstraintDefinitionDto dto =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            parameters, root);
    ConstraintDefinitionTimefoldInstance definition =
        ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(definition.evaluationFunction().apply(arguments));
    assertEquals(definition.defaultType(), dto.defaultType());
    assertEquals(definition.name(), dto.name());

    AbstractSyntaxTreeDto nameThrows = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "student");
    AbstractSyntaxTreeDto attributeThrows = new ValueDto<>(UcdlToken.ATTRIBUTE, "studentGroups");
    AbstractSyntaxTreeDto elementThrows =
        new ElementDto(UcdlToken.ELEMENT, nameThrows, List.of(attributeThrows),
            ResourceType.STUDENT_GROUP);

    AbstractSyntaxTreeDto rootThrows = new OperatorDto(UcdlToken.IS_EMPTY, List.of(elementThrows));
    ConstraintDefinitionDto dtoThrows =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            parameters, rootThrows);

    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(dtoThrows).evaluationFunction()
            .apply(arguments));
  }

  @Test
  void getConstraintDefinitionSize() {
    AbstractSyntaxTreeDto number = new ValueDto<>(UcdlToken.NUMBER, -1);
    AbstractSyntaxTreeDto elementName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto element =
        new ElementDto(UcdlToken.ELEMENT, elementName, new ArrayList<>(), ResourceType.TIMETABLE);
    AbstractSyntaxTreeDto size = new OperatorDto(UcdlToken.SIZE, List.of(element));


    AbstractSyntaxTreeDto root = new OperatorDto(UcdlToken.EQUALS, List.of(number, size));
    ConstraintDefinitionDto dto =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            new LinkedHashMap<>(), root);
    ConstraintDefinitionTimefoldInstance definition =
        ConstraintDefinitionFactory.getConstraintDefinition(dto);

    assertFalse(
        definition.evaluationFunction().apply(List.of(new TimetableSolutionTimefoldInstance())));

    AbstractSyntaxTreeDto sizeThrows = new OperatorDto(UcdlToken.SIZE, List.of(element, element));
    AbstractSyntaxTreeDto rootThrows =
        new OperatorDto(UcdlToken.EQUALS, List.of(number, sizeThrows));
    ConstraintDefinitionDto dtoThrows =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            new LinkedHashMap<>(), rootThrows);
    assertThrows(IllegalStateException.class,
        () -> ConstraintDefinitionFactory.getConstraintDefinition(dtoThrows).evaluationFunction()
            .apply(List.of(new TimetableSolutionTimefoldInstance())));
  }

  @Test
  void getConstraintDefinitionSet() {
    AbstractSyntaxTreeDto trueAst = new ValueDto<>(UcdlToken.BOOL_VALUE, true);
    AbstractSyntaxTreeDto elementName = new ValueDto<>(UcdlToken.VALUE_REFERENCE, "this");
    AbstractSyntaxTreeDto element =
        new ElementDto(UcdlToken.ELEMENT, elementName, List.of(), ResourceType.TIMETABLE);
    AbstractSyntaxTreeDto filter = new OperatorDto(UcdlToken.FILTER, List.of(trueAst));
    AbstractSyntaxTreeDto set =
        new SetDto(UcdlToken.RESOURCE_SET, ResourceType.TIMETABLE, element, List.of(filter));
    AbstractSyntaxTreeDto root = new OperatorDto(UcdlToken.IS_EMPTY, List.of(set));

    ConstraintDefinitionDto dto =
        new ConstraintDefinitionDto("test", "test", RewardPenalize.HARD_PENALIZE,
            new LinkedHashMap<>(), root);
    ConstraintDefinitionTimefoldInstance definition =
        ConstraintDefinitionFactory.getConstraintDefinition(dto);
    assertFalse(
        definition.evaluationFunction()
            .apply(new ArrayList<>(List.of(new TimetableSolutionTimefoldInstance()))));


  }
  //todo: implement tests for filters
}
