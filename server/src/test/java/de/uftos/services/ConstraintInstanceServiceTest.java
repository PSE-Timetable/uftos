package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.ResourceType;
import de.uftos.dto.Weekday;
import de.uftos.dto.requestdtos.ConstraintArgumentRequestDto;
import de.uftos.dto.requestdtos.ConstraintInstanceRequestDto;
import de.uftos.dto.responsedtos.ConstraintInstancesResponseDto;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.entities.ConstraintArgument;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintParameter;
import de.uftos.entities.ConstraintSignature;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    ConstraintSignature constraintSignatureManyParams = new ConstraintSignature();
    constraintSignatureManyParams.setName("many params");

    ConstraintSignature constraintSignatureInvalid = new ConstraintSignature();
    constraintSignatureInvalid.setName("invalid params");

    //PARAMETERS

    ConstraintParameter constraintParameterTeacher = new ConstraintParameter();
    constraintParameterTeacher.setParameterType(ResourceType.TEACHER);
    constraintParameterTeacher.setParameterName("teacher123");
    constraintParameterTeacher.setId("teacherParamId");

    ConstraintParameter constraintParameterTag = new ConstraintParameter();
    constraintParameterTag.setParameterType(ResourceType.TAG);
    constraintParameterTag.setParameterName("tag123");
    constraintParameterTag.setId("tagParamId");

    ConstraintParameter constraintParameterRoom = new ConstraintParameter();
    constraintParameterRoom.setParameterType(ResourceType.ROOM);
    constraintParameterRoom.setParameterName("room123");
    constraintParameterRoom.setId("roomParamId");

    ConstraintParameter constraintParameterStudent = new ConstraintParameter();
    constraintParameterStudent.setParameterType(ResourceType.STUDENT);
    constraintParameterStudent.setParameterName("student123");
    constraintParameterStudent.setId("studentParamId");

    ConstraintParameter constraintParameterLesson = new ConstraintParameter();
    constraintParameterLesson.setParameterType(ResourceType.LESSON);
    constraintParameterLesson.setParameterName("lesson123");
    constraintParameterLesson.setId("lessonParamId");

    ConstraintParameter constraintParameterGrade = new ConstraintParameter();
    constraintParameterGrade.setParameterType(ResourceType.GRADE);
    constraintParameterGrade.setParameterName("grade123");
    constraintParameterGrade.setId("gradeParamId");

    ConstraintParameter constraintParameterSubject = new ConstraintParameter();
    constraintParameterSubject.setParameterType(ResourceType.SUBJECT);
    constraintParameterSubject.setParameterName("subject123");
    constraintParameterSubject.setId("subjectParamId");

    ConstraintParameter constraintParameterGroup = new ConstraintParameter();
    constraintParameterGroup.setParameterType(ResourceType.STUDENT_GROUP);
    constraintParameterGroup.setParameterName("group123");
    constraintParameterGroup.setId("groupParamId");

    ConstraintParameter constraintParameterTimeslot = new ConstraintParameter();
    constraintParameterTimeslot.setParameterType(ResourceType.TIMESLOT);
    constraintParameterTimeslot.setParameterName("timeslot123");
    constraintParameterTimeslot.setId("timeslotParamId");

    ConstraintParameter constraintParameterNumber = new ConstraintParameter();
    constraintParameterNumber.setParameterType(ResourceType.NUMBER);
    constraintParameterNumber.setParameterName("number123");
    constraintParameterNumber.setId("numberParamId");

    //ARGUMENTS

    ConstraintArgument constraintArgumentTeacher = new ConstraintArgument();
    constraintArgumentTeacher.setId("teacherArgId");
    constraintArgumentTeacher.setValue("teacherId1");
    constraintArgumentTeacher.setConstraintParameter(constraintParameterTeacher);

    constraintParameterTeacher.setConstraintArguments(List.of(constraintArgumentTeacher));

    ConstraintArgument constraintArgumentTag = new ConstraintArgument();
    constraintArgumentTag.setId("tagArgId");
    constraintArgumentTag.setValue("tagId1");
    constraintArgumentTag.setConstraintParameter(constraintParameterTag);

    constraintParameterTag.setConstraintArguments(List.of(constraintArgumentTag));

    ConstraintArgument constraintArgumentRoom = new ConstraintArgument();
    constraintArgumentRoom.setId("roomArgId");
    constraintArgumentRoom.setValue("roomId1");
    constraintArgumentRoom.setConstraintParameter(constraintParameterRoom);

    constraintParameterRoom.setConstraintArguments(List.of(constraintArgumentRoom));

    ConstraintArgument constraintArgumentStudent = new ConstraintArgument();
    constraintArgumentStudent.setId("studentArgId");
    constraintArgumentStudent.setValue("studentId1");
    constraintArgumentStudent.setConstraintParameter(constraintParameterStudent);

    constraintParameterStudent.setConstraintArguments(List.of(constraintArgumentStudent));

    ConstraintArgument constraintArgumentLesson = new ConstraintArgument();
    constraintArgumentLesson.setId("lessonArgId");
    constraintArgumentLesson.setValue("lessonId1");
    constraintArgumentLesson.setConstraintParameter(constraintParameterLesson);

    constraintParameterLesson.setConstraintArguments(List.of(constraintArgumentLesson));

    ConstraintArgument constraintArgumentGrade = new ConstraintArgument();
    constraintArgumentGrade.setId("gradeArgId");
    constraintArgumentGrade.setValue("gradeId1");
    constraintArgumentGrade.setConstraintParameter(constraintParameterGrade);

    constraintParameterGrade.setConstraintArguments(List.of(constraintArgumentGrade));

    ConstraintArgument constraintArgumentSubject = new ConstraintArgument();
    constraintArgumentSubject.setId("subjectArgId");
    constraintArgumentSubject.setValue("subjectId1");
    constraintArgumentSubject.setConstraintParameter(constraintParameterSubject);

    constraintParameterSubject.setConstraintArguments(List.of(constraintArgumentSubject));

    ConstraintArgument constraintArgumentGroup = new ConstraintArgument();
    constraintArgumentGroup.setId("groupArgId");
    constraintArgumentGroup.setValue("groupId1");
    constraintArgumentGroup.setConstraintParameter(constraintParameterGroup);

    constraintParameterGroup.setConstraintArguments(List.of(constraintArgumentGroup));

    ConstraintArgument constraintArgumentTimeslot = new ConstraintArgument();
    constraintArgumentTimeslot.setId("timeslotArgId");
    constraintArgumentTimeslot.setValue("timeslotId1");
    constraintArgumentTimeslot.setConstraintParameter(constraintParameterTimeslot);

    constraintParameterTimeslot.setConstraintArguments(List.of(constraintArgumentTimeslot));

    ConstraintArgument constraintArgumentNumber = new ConstraintArgument();
    constraintArgumentNumber.setId("numberArgId");
    constraintArgumentNumber.setValue("numberId1");
    constraintArgumentNumber.setConstraintParameter(constraintParameterNumber);

    constraintParameterNumber.setConstraintArguments(List.of(constraintArgumentNumber));


    ConstraintInstance constraintInstance = new ConstraintInstance();
    constraintInstance.setId("123");
    constraintInstance.setArguments(List.of(constraintArgumentTeacher));

    ConstraintInstance constraintInstanceManyArgs = new ConstraintInstance();
    constraintInstanceManyArgs.setId("manyArgs");
    constraintInstanceManyArgs.setArguments(
        List.of(constraintArgumentTeacher, constraintArgumentGrade,
            constraintArgumentGroup, constraintArgumentLesson, constraintArgumentRoom,
            constraintArgumentStudent, constraintArgumentSubject, constraintArgumentTag,
            constraintArgumentTimeslot));

    ConstraintInstance constraintInstanceInvalidArgs = new ConstraintInstance();
    constraintInstanceInvalidArgs.setId("invalidArgs");
    constraintInstanceInvalidArgs.setArguments(List.of(constraintArgumentNumber));

    constraintSignatureManyParams.setInstances(
        new ArrayList<>(List.of(constraintInstanceManyArgs)));
    constraintSignatureManyParams.setParameters(
        List.of(constraintParameterGrade, constraintParameterGroup,
            constraintParameterLesson, constraintParameterTeacher, constraintParameterRoom,
            constraintParameterStudent, constraintParameterSubject, constraintParameterTag,
            constraintParameterTimeslot));

    constraintSignatureInvalid.setInstances(
        new ArrayList<>(List.of(constraintInstanceInvalidArgs)));
    constraintSignatureInvalid.setParameters(List.of(constraintParameterNumber));

    constraintSignature.setInstances(new ArrayList<>(List.of(constraintInstance)));
    constraintSignature.setParameters(List.of(constraintParameterTeacher));


    when(constraintInstanceRepository.findById("123")).thenReturn(
        Optional.of(constraintInstance));
    when(constraintInstanceRepository.findById(constraintInstanceManyArgs.getId())).thenReturn(
        Optional.of(constraintInstanceManyArgs));
    when(constraintInstanceRepository.findById(constraintInstanceInvalidArgs.getId())).thenReturn(
        Optional.of(constraintInstanceInvalidArgs));
    when(constraintSignatureRepository.findById(constraintSignature.getName())).thenReturn(
        Optional.of(constraintSignature));

    Page<ConstraintInstance> instancePage = new PageImpl<>(List.of(constraintInstance));
    when(constraintInstanceRepository.findByArguments(eq("teacherArgId"),
        any(Pageable.class))).thenReturn(instancePage);

    when(
        constraintSignatureRepository.findById(constraintSignatureManyParams.getName())).thenReturn(
        Optional.of(constraintSignatureManyParams));
    when(constraintSignatureRepository.findById(constraintSignatureInvalid.getName())).thenReturn(
        Optional.of(constraintSignatureInvalid));
    when(constraintSignatureRepository.findInstancesBySignatureId(eq("test constraint"),
        any(Pageable.class))).thenReturn(instancePage);


    Timeslot timeslot = new Timeslot("timeslotId1");
    timeslot.setDay(Weekday.TUESDAY);
    Teacher teacher = new Teacher("teacherId1");
    Tag tag = new Tag("tagId1");
    Room room = new Room("roomId1");
    Grade grade = new Grade("gradeId1");
    Lesson lesson = new Lesson(0, "teacher", "group", "room", "timeslot",
        "subject", "timetable", "2024");
    Student student = new Student("studentId1");
    Subject subject = new Subject("subjectId1");
    StudentGroup group = new StudentGroup("groupId1");

    when(studentRepository.findById("studentId1")).thenReturn(Optional.of(student));
    when(subjectRepository.findById("subjectId1")).thenReturn(Optional.of(subject));
    when(studentGroupRepository.findById("groupId1")).thenReturn(Optional.of(group));
    when(teacherRepository.findById("teacherId1")).thenReturn(Optional.of(teacher));
    when(teacherRepository.findById("teacherId2")).thenReturn(Optional.empty());
    when(tagRepository.findById("tagId1")).thenReturn(Optional.of(tag));
    when(roomRepository.findById("roomId1")).thenReturn(Optional.of(room));
    when(gradeRepository.findById("gradeId1")).thenReturn(Optional.of(grade));
    when(lessonRepository.findById("lessonId1")).thenReturn(Optional.of(lesson));
    when(timeslotRepository.findById("timeslotId1")).thenReturn(Optional.of(timeslot));
  }

  @Test
  void constrainInstanceById() {
    ConstraintInstancesResponseDto constraintInstanceResponse =
        constraintInstanceService.getById("test constraint", "123");
    assertNotNull(constraintInstanceResponse);
    assertEquals(1, constraintInstanceResponse.constraintInstances().size());
    ConstraintInstancesResponseDto.SlimInstance constraintInstance1 =
        constraintInstanceResponse.constraintInstances().getFirst();
    assertEquals("123", constraintInstance1.id());
    assertFalse(constraintInstance1.arguments().isEmpty());
  }

  @Test
  void constrainInstanceManyArgsById() {
    ConstraintInstancesResponseDto constraintInstanceResponse =
        constraintInstanceService.getById("many params", "manyArgs");
    assertNotNull(constraintInstanceResponse);
    assertEquals(1, constraintInstanceResponse.constraintInstances().size());
    ConstraintInstancesResponseDto.SlimInstance constraintInstance1 =
        constraintInstanceResponse.constraintInstances().getFirst();
    assertEquals("manyArgs", constraintInstance1.id());
    assertFalse(constraintInstance1.arguments().isEmpty());
  }

  @Test
  void constrainInstanceInvalidById() {
    assertThrows(ResponseStatusException.class,
        () -> constraintInstanceService.getById("invalid params", "invalidArgs"));
  }

  @Test
  void nonExistentParameter() {
    ConstraintArgumentRequestDto arg =
        new ConstraintArgumentRequestDto("teacher123", "teacherId2");
    ConstraintInstanceRequestDto constraintInstanceRequestDto =
        new ConstraintInstanceRequestDto(List.of(arg), RewardPenalize.HARD_PENALIZE);

    assertThrows(ResponseStatusException.class,
        () -> constraintInstanceService.create("test constraint", constraintInstanceRequestDto));
  }

  @Test
  void getConstraintInstancesNoArgs() {
    ConstraintInstancesResponseDto result =
        constraintInstanceService.get("test constraint", PageRequest.of(0, 10), Optional.empty());
    assertEquals(1, result.totalElements());
    List<ConstraintInstancesResponseDto.SlimInstance> instances = result.constraintInstances();
    assertEquals("123", instances.getFirst().id());
  }

  @Test
  void getConstraintInstancesWithArgs() {
    ConstraintInstancesResponseDto result =
        constraintInstanceService.get("test constraint", PageRequest.of(0, 10),
            Optional.of("teacherArgId"));
    assertEquals(1, result.totalElements());
    List<ConstraintInstancesResponseDto.SlimInstance> instances = result.constraintInstances();
    assertEquals("123", instances.getFirst().id());
  }

  @Test
  void getConstraintInstancesWithEmptyArg() {
    ConstraintInstancesResponseDto result =
        constraintInstanceService.get("test constraint", PageRequest.of(0, 10),
            Optional.of(""));
    assertEquals(1, result.totalElements());
    List<ConstraintInstancesResponseDto.SlimInstance> instances = result.constraintInstances();
    assertEquals("123", instances.getFirst().id());
  }

  @Test
  void tooManyParameter() {
    ConstraintArgumentRequestDto arg1 =
        new ConstraintArgumentRequestDto("teacher123", "teacherId1");
    ConstraintArgumentRequestDto arg2 =
        new ConstraintArgumentRequestDto("teacher456", "teacherId1");
    ConstraintInstanceRequestDto constraintInstanceRequestDto =
        new ConstraintInstanceRequestDto(List.of(arg1, arg2), RewardPenalize.HARD_PENALIZE);

    assertThrows(ResponseStatusException.class,
        () -> constraintInstanceService.create("test constraint", constraintInstanceRequestDto));
  }

  @Test
  void createConstraintInstance() {
    ConstraintArgumentRequestDto arg =
        new ConstraintArgumentRequestDto("teacher123", "teacherId1");
    ConstraintInstanceRequestDto constraintInstanceRequestDto =
        new ConstraintInstanceRequestDto(List.of(arg), RewardPenalize.HARD_PENALIZE);

    assertDoesNotThrow(
        () -> constraintInstanceService.create("test constraint", constraintInstanceRequestDto));
  }

  @Test
  void createConstraintInstanceInvalidSignatureId() {
    ConstraintArgumentRequestDto arg =
        new ConstraintArgumentRequestDto("teacher123", "teacherId1");
    ConstraintInstanceRequestDto constraintInstanceRequestDto =
        new ConstraintInstanceRequestDto(List.of(arg), RewardPenalize.HARD_PENALIZE);

    assertThrows(ResponseStatusException.class,
        () -> constraintInstanceService.create("nonExistentId", constraintInstanceRequestDto));
  }

  @Test
  void createConstraintInstanceWrongParamName() {
    ConstraintArgumentRequestDto arg =
        new ConstraintArgumentRequestDto("wrongParamName", "teacherId1");
    //this parameter name does not exist for the constraint signature
    ConstraintInstanceRequestDto constraintInstanceRequestDto =
        new ConstraintInstanceRequestDto(List.of(arg), RewardPenalize.HARD_PENALIZE);

    assertThrows(ResponseStatusException.class,
        () -> constraintInstanceService.create("test constraint", constraintInstanceRequestDto));
  }

  @Test
  void updateConstraintInstance() {
    ConstraintArgumentRequestDto arg =
        new ConstraintArgumentRequestDto("teacher123", "newTeacherId");
    ConstraintInstanceRequestDto constraintInstanceRequestDto =
        new ConstraintInstanceRequestDto(List.of(arg), RewardPenalize.SOFT_PENALIZE);

    constraintInstanceService.update("test constraint", "123", constraintInstanceRequestDto);

    ArgumentCaptor<ConstraintInstance> constraintInstanceCap =
        ArgumentCaptor.forClass(ConstraintInstance.class);
    verify(constraintInstanceRepository, times(1)).save(constraintInstanceCap.capture());

    ConstraintInstance constraintInstance = constraintInstanceCap.getValue();
    assertNotNull(constraintInstance);

    assertEquals(RewardPenalize.SOFT_PENALIZE, constraintInstance.getType());

    assertEquals(1, constraintInstance.getArguments().size());
    assertEquals("newTeacherId", constraintInstance.getArguments().getFirst().getValue());
  }

  @Test
  void deleteExistentInstance() {
    assertDoesNotThrow(() -> constraintInstanceService.delete("test constraint", List.of("123")));
    //to delete an instance, it is simply removed from the signature's list of instances
    ArgumentCaptor<ConstraintSignature> constraintSignatureCap =
        ArgumentCaptor.forClass(ConstraintSignature.class);
    verify(constraintSignatureRepository, times(1)).save(constraintSignatureCap.capture());

    ConstraintSignature constraintSignature = constraintSignatureCap.getValue();
    assertEquals(0, constraintSignature.getInstances().size());
  }

  @Test
  void deleteNonExistentInstance() {
    assertThrows(ResponseStatusException.class,
        () -> constraintInstanceService.delete("nonExistentId", List.of("123")));
  }
}
