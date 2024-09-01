package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.StudentGroupRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.dto.responsedtos.StudentGroupResponseDto;
import de.uftos.entities.Break;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timetable;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.TimetableRepository;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudentGroupServiceTests {
  private final StudentGroupRequestDto requestDto =
      new StudentGroupRequestDto("testName", List.of("studentId1"), List.of("gradeId1"),
          List.of("tagId1"), List.of("subjectId1"));

  private final StudentGroupRequestDto requestDtoEmptyName =
      new StudentGroupRequestDto("", List.of("studentId1"), List.of("gradeId1"),
          List.of("tagId1"), List.of("subjectId1"));
  private final StudentGroupRequestDto requestDtoInvalidGrade =
      new StudentGroupRequestDto("groupName", List.of("studentId1"), List.of("nonExistentGrade"),
          List.of("tagId1"), List.of("subjectId1"));

  @Mock
  private Grade gradeNoGroupsMock;
  @Mock
  private Grade gradeForUpdateMock;
  @Mock
  private StudentGroupRepository studentGroupRepository;

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private GradeRepository gradeRepository;

  @Mock
  private ServerRepository serverRepository;

  @Mock
  private ConstraintSignatureRepository signatureRepository;

  @Mock
  private ConstraintInstanceRepository instanceRepository;

  @Mock
  private LessonRepository lessonRepository;

  @Mock
  private TimetableRepository timetableRepository;

  @InjectMocks
  private StudentGroupService studentGroupService;


  @BeforeEach
  void setUp() {
    StudentGroup studentGroup1 =
        new StudentGroup("Ethics", List.of("studentId0", "studentId1"), List.of(),
            List.of());
    studentGroup1.setId("123");

    StudentGroup studentGroup2 =
        new StudentGroup("Religion", List.of("studentId1", "studentId2", "studentId3"),
            List.of(),
            List.of("T1", "T2"));
    studentGroup2.setId("456");
    studentGroup2.setLessons(Collections.emptyList());

    StudentGroup studentGroup3 =
        new StudentGroup("testName", List.of("studentId1"), List.of("tagId1"),
            List.of("subjectId1"));

    Grade grade1 = new Grade("gradeId1");
    grade1.setStudentGroups(new ArrayList<>(List.of(studentGroup1, studentGroup2, studentGroup3)));

    when(gradeRepository.findAllById(List.of("gradeId1"))).thenReturn(List.of(grade1));

    studentGroup1.setGrades(List.of(grade1));
    studentGroup2.setGrades(List.of(grade1));
    studentGroup3.setGrades(List.of(grade1));

    Subject subject = new Subject("789");

    Room room1 = new Room("534");
    Room room2 = new Room("574");

    Teacher teacher1 = new Teacher("T1");
    Teacher teacher2 = new Teacher("T2");

    Timetable timetable = new Timetable("timetable");
    timetable.setId("timetableId");

    Lesson lesson1 = createLesson(teacher1, room1, studentGroup1, "2024", subject, timetable);
    Lesson lesson2 = createLesson(teacher2, room1, studentGroup1, "2022", subject, timetable);
    Lesson lesson3 = createLesson(teacher1, room2, studentGroup2, "2024", subject, timetable);

    studentGroup1.setLessons(List.of(lesson1, lesson2, lesson3));

    Student student = new Student("Firstname", "Lastname", List.of("tagId1"));
    student.setId("studentId1");

    Student student1 = new Student("Firstname1", "Lastname1", List.of("tagId1"));
    student1.setId("studentId2");
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(studentRepository.findById(student1.getId())).thenReturn(Optional.of(student1));
    when(studentRepository.findAll()).thenReturn(List.of(student, student1));

    Break[] breaks = {};
    Server server =
        new Server(new TimetableMetadata(45, 8, "8:00", breaks), "2024", "test@uftos.de");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(studentGroupRepository.findById("123")).thenReturn(Optional.of(studentGroup1));
    when(studentGroupRepository.findById("456")).thenReturn(Optional.of(studentGroup2));

    studentGroup3.setId("789");
    when(studentGroupRepository.save(any(StudentGroup.class))).thenReturn(studentGroup3);
    when(studentGroupRepository.findById("789")).thenReturn(Optional.of(studentGroup3));
  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> studentGroupService.getById("nonExistingId"));
  }

  @Test
  void getByExistentId() {
    StudentGroupResponseDto result = studentGroupService.getById("123");
    assertEquals("Ethics", result.name());
    assertEquals(2, result.students().size());
    assertEquals("studentId0", result.students().getFirst().getId());
    assertEquals("studentId1", result.students().get(1).getId());
    assertEquals(1, result.grades().size());
    assertEquals("gradeId1", result.grades().getFirst().id());
    assertEquals(0, result.tags().size());
  }

  @Test
  void emptyLessons() {
    assertDoesNotThrow(() -> studentGroupService.getLessonsById("456"));
    LessonResponseDto result = studentGroupService.getLessonsById("456");
    assertResultArraySizes(result, 0, 0, 0, 0);
  }

  @Test
  void lessonsById() {
    LessonResponseDto result = studentGroupService.getLessonsById("123");
    assertResultArraySizes(result, 1, 2, 2, 1);
    assertAll("Testing whether the sizes of the arrays are correct",
        () -> assertEquals(3, result.grades().getFirst().studentGroupIds().size()),
        () -> assertEquals(4, result.grades().getFirst().studentIds().size())
    );

    List<String> roomIds = result.rooms().stream().map(Room::getId).toList();
    assertAll("Testing whether all the rooms are there",
        () -> assertEquals(2, roomIds.size()),
        () -> assertTrue(roomIds.contains("534")),
        () -> assertTrue(roomIds.contains("574"))
    );

    assertAll("Testing whether all the student groups are there",
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("123")),
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("456"))
    );

    assertAll("Testing whether all the students are there",
        () -> assertTrue(result.grades().getFirst().studentIds().contains("studentId0")),
        () -> assertTrue(result.grades().getFirst().studentIds().contains("studentId1")),
        () -> assertTrue(result.grades().getFirst().studentIds().contains("studentId3"))
    );
  }

  @Test
  void createGroup() {
    StudentGroupResponseDto studentGroup = studentGroupService.create(requestDto);

    assertNotNull(studentGroup);
    assertEquals("testName", studentGroup.name());

    assertEquals(1, studentGroup.students().size());
    assertEquals("studentId1", studentGroup.students().getFirst().getId());

    assertEquals(1, studentGroup.grades().size());
    assertEquals("gradeId1", studentGroup.grades().getFirst().id());

    assertEquals(1, studentGroup.tags().size());
    assertEquals("tagId1", studentGroup.tags().getFirst().getId());

    assertEquals(1, studentGroup.subjects().size());
    assertEquals("subjectId1", studentGroup.subjects().getFirst().getId());
  }

  @Test
  void createGroupEmptyName() {
    assertThrows(ResponseStatusException.class,
        () -> studentGroupService.create(requestDtoEmptyName));
  }

  @Test
  void createGroupInvalidGrade() {
    assertThrows(ResponseStatusException.class,
        () -> studentGroupService.create(requestDtoInvalidGrade));
  }

  @Test
  void updateGroup() {
    StudentGroupRequestDto requestDto =
        new StudentGroupRequestDto("Ethics2", List.of("studentId0", "studentId1"),
            List.of("noGroupsId", "gradeForUpdate"), List.of(), List.of());
    studentGroupService.update("123", requestDto);

    ArgumentCaptor<StudentGroup> studentGroupCap = ArgumentCaptor.forClass(StudentGroup.class);
    verify(studentGroupRepository, times(1)).save(studentGroupCap.capture());

    StudentGroup studentGroup = studentGroupCap.getValue();
    assertNotNull(studentGroup);
    assertEquals("Ethics2", studentGroup.getName());
    assertEquals("123", studentGroup.getId());
  }

  @Test
  void updateGroupEmptyName() {
    assertThrows(ResponseStatusException.class,
        () -> studentGroupService.update("123", requestDtoEmptyName));
  }

  @Test
  void addNonExistentStudent() {
    assertThrows(ResponseStatusException.class,
        () -> studentGroupService.addStudents("123", List.of("studentId3")));
  }

  @Test
  void addExistentStudent() {
    assertDoesNotThrow(() -> studentGroupService.addStudents("123", List.of("studentId1")));

    ArgumentCaptor<StudentGroup> studentGroupCap = ArgumentCaptor.forClass(StudentGroup.class);
    verify(studentGroupRepository, times(1)).save(studentGroupCap.capture());

    StudentGroup studentGroup = studentGroupCap.getValue();
    assertTrue(studentGroup.getStudents().stream().anyMatch(s -> s.getId().equals("studentId1")));
  }

  @Test
  void removeStudent() {
    studentGroupService.removeStudents("456", List.of("studentId2"));

    ArgumentCaptor<StudentGroup> studentGroupCap = ArgumentCaptor.forClass(StudentGroup.class);
    verify(studentGroupRepository, times(1)).save(studentGroupCap.capture());

    StudentGroup studentGroup = studentGroupCap.getValue();
    assertFalse(studentGroup.getStudents().stream().anyMatch(s -> s.getId().equals("studentId2")));
  }

  @Test
  void deleteExistentGroup() {
    assertDoesNotThrow(() -> studentGroupService.delete("123"));
    ArgumentCaptor<StudentGroup> studentGroupCap = ArgumentCaptor.forClass(StudentGroup.class);
    verify(studentGroupRepository, times(1)).delete(studentGroupCap.capture());

    StudentGroup studentGroup = studentGroupCap.getValue();
    assertEquals("123", studentGroup.getId());
  }

  @Test
  void deleteNonExistentGroup() {
    assertThrows(ResponseStatusException.class, () -> studentGroupService.delete("123456"));
  }


  private Lesson createLesson(Teacher teacher, Room room, StudentGroup studentGroup, String number,
                              Subject subject, Timetable timetable) {
    Lesson lesson = new Lesson();
    lesson.setTeacher(teacher);
    lesson.setRoom(room);
    lesson.setStudentGroup(studentGroup);
    lesson.setYear(number);
    lesson.setSubject(subject);
    lesson.setTimetable(timetable);
    return lesson;
  }

  private void assertResultArraySizes(LessonResponseDto result, int teachers, int lessons,
                                      int rooms, int grades) {
    assertAll("Testing whether the sizes of the arrays are correct",
        () -> assertEquals(teachers, result.teachers().size()),
        () -> assertEquals(lessons, result.lessons().size()),
        () -> assertEquals(rooms, result.rooms().size()),
        () -> assertEquals(grades, result.grades().size())
    );
  }
}
