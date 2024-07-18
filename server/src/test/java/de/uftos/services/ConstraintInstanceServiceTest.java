package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import de.uftos.dto.ConstraintInstanceRequestDto;
import de.uftos.dto.ConstraintInstanceResponseDto;
import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.entities.ConstraintArgument;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintParameter;
import de.uftos.entities.ConstraintSignature;
import de.uftos.entities.Teacher;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConstraintInstanceServiceTest {

  @Mock
  private ConstraintInstanceRepository constraintInstanceRepository;
  @Mock
  private ConstraintSignatureRepository constraintSignatureRepository;
  @Mock
  private GradeRepository gradeRepository;
  @Mock
  private LessonRepository lessonRepository;
  @Mock
  private RoomRepository roomRepository;
  @Mock
  private StudentRepository studentRepository;
  @Mock
  private StudentGroupRepository studentGroupRepository;
  @Mock
  private SubjectRepository subjectRepository;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private TeacherRepository teacherRepository;
  @Mock
  private TimeslotRepository timeslotRepository;

  @InjectMocks
  private ConstraintInstanceService constraintInstanceService;

  @BeforeEach
  void setUp() {
    ConstraintSignature constraintSignature = new ConstraintSignature();
    constraintSignature.setName("test constraint");

    ConstraintParameter constraintParameter = new ConstraintParameter();
    constraintParameter.setParameterType(ResourceType.TEACHER);
    constraintParameter.setParameterName("teacher123");
    constraintParameter.setId("456");

    ConstraintArgument constraintArgument = new ConstraintArgument();
    constraintArgument.setId("789");
    constraintArgument.setValue("teacherId1");
    constraintArgument.setConstraintParameter(constraintParameter);

    constraintParameter.setConstraintArguments(List.of(constraintArgument));

    ConstraintInstance constraintInstance = new ConstraintInstance();
    constraintInstance.setId("123");
    constraintInstance.setSignature(constraintSignature);
    constraintInstance.setArguments(List.of(constraintArgument));

    constraintSignature.setInstances(List.of(constraintInstance));
    constraintSignature.setParameters(List.of(constraintParameter));

    when(constraintInstanceRepository.findById("123")).thenReturn(
        Optional.of(constraintInstance));
    when(constraintInstanceRepository.findBySignatureAndId(constraintSignature, "123")).thenReturn(
        Optional.of(constraintInstance));
    when(constraintSignatureRepository.findById("test constraint")).thenReturn(
        Optional.of(constraintSignature));

    Teacher teacher = new Teacher("teacherId1");

    when(teacherRepository.findById("teacherId1")).thenReturn(Optional.of(teacher));
    when(teacherRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(gradeRepository.findById("teacherId1")).thenReturn(Optional.empty());
    when(gradeRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(lessonRepository.findById("teacherId1")).thenReturn(Optional.empty());
    when(lessonRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(studentGroupRepository.findById("teacherId1")).thenReturn(Optional.empty());
    when(studentGroupRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(subjectRepository.findById("teacherId1")).thenReturn(Optional.empty());
    when(subjectRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(tagRepository.findById("teacherId1")).thenReturn(Optional.empty());
    when(tagRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(roomRepository.findById("teacherId1")).thenReturn(Optional.empty());
    when(roomRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(studentRepository.findById("teacherId1")).thenReturn(Optional.empty());
    when(studentRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(timeslotRepository.findById("teacherId1")).thenReturn(Optional.empty());
    when(timeslotRepository.findById("teacherId2")).thenReturn(Optional.empty());
  }

  @Test
  void constrainInstanceById() {
    ConstraintInstanceResponseDto constraintInstanceResponse =
        constraintInstanceService.getById("test constraint", "123");
    assertNotNull(constraintInstanceResponse);
    assertEquals(1, constraintInstanceResponse.constraintInstances().size());
    ConstraintInstance constraintInstance1 =
        constraintInstanceResponse.constraintInstances().getFirst();
    assertEquals("123", constraintInstance1.getId());
    assertEquals("test constraint", constraintInstance1.getSignature().getName());
    assertFalse(constraintInstance1.getArguments().isEmpty());
  }

  @Test
  void nonExistentParameter() {
    ConstraintInstanceRequestDto constraintInstanceRequestDto = new ConstraintInstanceRequestDto(
        Map.of(
            "teacher123", "teacherId2"
        ), RewardPenalize.HARD_PENALIZE);

    assertThrows(ResponseStatusException.class,
        () -> constraintInstanceService.create("test constraint", constraintInstanceRequestDto));
  }

  @Test
  void tooManyParameter() {
    ConstraintInstanceRequestDto constraintInstanceRequestDto = new ConstraintInstanceRequestDto(
        Map.of(
            "teacher123", "teacherId1",
            "teacher456", "teacherId1"
        ), RewardPenalize.HARD_PENALIZE);

    assertThrows(ResponseStatusException.class,
        () -> constraintInstanceService.create("test constraint", constraintInstanceRequestDto));
  }

  @Test
  void createConstraintInstance() {
    ConstraintInstanceRequestDto constraintInstanceRequestDto = new ConstraintInstanceRequestDto(
        Map.of(
            "teacher123", "teacherId1"
        ), RewardPenalize.HARD_PENALIZE);

    assertDoesNotThrow(
        () -> constraintInstanceService.create("test constraint", constraintInstanceRequestDto));
  }
}
