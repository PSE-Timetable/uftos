package de.uftos.services;

import static de.uftos.utils.ClassCaster.getClassType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.StudentRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Tag;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudentServiceTest {
  @Mock
  private StudentGroup groupMock;

  @Mock
  StudentGroupRepository studentGroupRepository;

  @Mock
  private ServerRepository serverRepository;

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private ConstraintSignatureRepository constraintSignatureRepository;

  @InjectMocks
  private StudentService studentService;

  @BeforeEach
  void setUp() {
    Tag tag = new Tag("", "tagName");
    tag.setId("tagId");

    StudentGroup group =
        new StudentGroup("group", new ArrayList<>(List.of("123")), Collections.emptyList(),
            Collections.emptyList());
    group.setId("groupId");

    Lesson lesson =
        new Lesson(0, "teacherId", "groupId", "roomId", "timeslotId", "subjectId", "timetableId",
            "2024");
    lesson.setStudentGroup(groupMock);

    Lesson lessonWrongYear =
        new Lesson(0, "teacherId", "groupId", "roomId", "timeslotId", "subjectId", "timetableId",
            "1865");
    lessonWrongYear.setStudentGroup(groupMock);
    group.setLessons(new ArrayList<>(List.of(lesson, lessonWrongYear)));

    Student student = new Student("123", "Max", "Mustermann", new ArrayList<>(List.of(groupMock)),
        new ArrayList<>(List.of(tag)));

    when(groupMock.getGrades()).thenReturn(Collections.emptyList());
    when(groupMock.getLessons()).thenReturn(new ArrayList<>(List.of(lesson, lessonWrongYear)));
    when(groupMock.getStudents()).thenReturn(new ArrayList<>(List.of(student)));
    when(groupMock.getId()).thenReturn("groupId");
    Server server = new Server(null, "2024", "email");
    when(serverRepository.findAll()).thenReturn(new ArrayList<>(List.of(server)));
    when(studentRepository.findAll()).thenReturn(new ArrayList<>(List.of(student)));
    when(studentRepository.findById("123")).thenReturn(Optional.of(student));
    when(studentRepository.findAllById(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(student)));
    when(studentRepository.findAllById(List.of("nonExistentId", "123"))).thenReturn(
        new ArrayList<>(List.of(student)));
    when(constraintSignatureRepository.findAll(any(Specification.class))).thenReturn(
        Collections.emptyList());
    when(studentGroupRepository.findByStudents(any(Student.class))).thenReturn(
        Collections.emptyList());
    when(studentGroupRepository.findAll(any(Specification.class))).thenReturn(
        new ArrayList<>(List.of(groupMock)));
    when(studentRepository.save(any(Student.class))).thenReturn(student);
  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> studentService.getById("nonExistentId"));
  }

  @Test
  void getByExistentId() {
    Student result = studentService.getById("123");
    assertNotNull(result);
    assertEquals("123", result.getId());
    assertEquals("Max", result.getFirstName());
    assertEquals("Mustermann", result.getLastName());
    assertEquals(1, result.getGroups().size());
    assertEquals("groupId", result.getGroups().getFirst().getId());
    assertEquals(1, result.getTags().size());
    assertEquals("tagId", result.getTags().getFirst().getId());
  }

  @Test
  void createStudent() {
    StudentRequestDto requestDto =
        new StudentRequestDto("Max", "Mustermann", new ArrayList<>(List.of("tagId")));
    studentService.create(requestDto);

    ArgumentCaptor<Student> studentCap = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository, times(1)).save(studentCap.capture());

    Student student = studentCap.getValue();
    assertNotNull(student);
    assertEquals("Max", student.getFirstName());

    assertEquals("Mustermann", student.getLastName());

    assertEquals(1, student.getTags().size());
    assertEquals("tagId", student.getTags().getFirst().getId());
  }

  @Test
  void createEmptyFirstNameStudent() {
    StudentRequestDto requestDto = new StudentRequestDto("", "Mustermann",
        Collections.emptyList());
    assertThrows(ResponseStatusException.class,
        () -> studentService.create(requestDto));
  }

  @Test
  void createEmptyLastNameStudent() {
    StudentRequestDto requestDto = new StudentRequestDto("Max", "",
        Collections.emptyList());
    assertThrows(ResponseStatusException.class,
        () -> studentService.create(requestDto));
  }

  @Test
  void updateStudent() {
    StudentRequestDto requestDto =
        new StudentRequestDto("newFirstName", "newLastName",
            Collections.emptyList());
    studentService.update("123", requestDto);

    ArgumentCaptor<Student> studentCap = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository, times(1)).save(studentCap.capture());

    Student student = studentCap.getValue();
    assertNotNull(student);
    assertEquals("newFirstName", student.getFirstName());

    assertEquals("newLastName", student.getLastName());

    assertEquals(0, student.getTags().size());
  }

  @Test
  void updateEmptyFirstNameStudent() {
    StudentRequestDto requestDto = new StudentRequestDto("", "Mustermann",
        Collections.emptyList());
    assertThrows(ResponseStatusException.class,
        () -> studentService.update("123", requestDto));
  }

  @Test
  void updateEmptyLastNameStudent() {
    StudentRequestDto requestDto = new StudentRequestDto("Max", "",
        Collections.emptyList());
    assertThrows(ResponseStatusException.class,
        () -> studentService.update("123", requestDto));
  }

  @Test
  void deleteExistentStudent() {
    assertDoesNotThrow(() -> studentService.deleteStudents(new String[] {"123"}));
    ArgumentCaptor<List<String>> studentCap = ArgumentCaptor.forClass(getClassType());
    verify(studentRepository, times(1)).deleteAllById(studentCap.capture());

    List<String> student = studentCap.getValue();

    assertEquals(1, student.size());
    assertEquals("123", student.getFirst());
  }

  @Test
  void deleteNonExistentStudent() {
    assertThrows(ResponseStatusException.class,
        () -> studentService.deleteStudents(new String[] {"nonExistentId"}));
  }

  @Test
  void deleteStudentsSomeExistent() {
    assertThrows(ResponseStatusException.class,
        () -> studentService.deleteStudents(new String[] {"nonExistentId", "123"}));
  }

  @Test
  void lessonsById() {
    LessonResponseDto result = studentService.getLessonsById("123");
    assertResultArraySizes(result, 1, 1, 1, 0);
    assertAll("Testing whether the sizes of the arrays are correct",
        () -> assertEquals(1, result.groups().size()),
        () -> assertEquals(1, result.groups().getFirst().students().size()),
        () -> assertEquals("groupId", result.groups().getFirst().id())
    );

    List<String> roomIds = result.rooms().stream().map(Room::getId).toList();
    assertAll("Testing whether all the rooms are there",
        () -> assertEquals(1, roomIds.size()),
        () -> assertTrue(roomIds.contains("roomId"))
    );
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
