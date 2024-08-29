package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.StudentRequestDto;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Tag;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
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
public class StudentServiceTest {
  @Mock
  StudentGroupRepository studentGroupRepository;

  @Mock
  private StudentRepository studentRepository;

  @InjectMocks
  private StudentService studentService;

  @BeforeEach
  void setUp() {
    Tag tag = new Tag("", "tagName");
    tag.setId("tagId");

    StudentGroup group = new StudentGroup("group", List.of("123"), List.of(), List.of());
    group.setId("groupId");

    Student student = new Student("123", "Max", "Mustermann", List.of(group), List.of(tag));

    when(studentRepository.findAll()).thenReturn(List.of(student));
    when(studentRepository.findById("123")).thenReturn(Optional.of(student));
    when(studentGroupRepository.findByStudents(any(Student.class))).thenReturn(
        Collections.emptyList());
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
        new StudentRequestDto("Max", "Mustermann", List.of("tagId"));
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
        List.of());
    assertThrows(ResponseStatusException.class,
        () -> studentService.create(requestDto));
  }

  @Test
  void createEmptyLastNameStudent() {
    StudentRequestDto requestDto = new StudentRequestDto("Max", "",
        List.of());
    assertThrows(ResponseStatusException.class,
        () -> studentService.create(requestDto));
  }

  @Test
  void updateStudent() {
    StudentRequestDto requestDto =
        new StudentRequestDto("newFirstName", "newLastName",
            List.of());
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
        List.of());
    assertThrows(ResponseStatusException.class,
        () -> studentService.update("123", requestDto));
  }

  @Test
  void updateEmptyLastNameStudent() {
    StudentRequestDto requestDto = new StudentRequestDto("Max", "",
        List.of());
    assertThrows(ResponseStatusException.class,
        () -> studentService.update("123", requestDto));
  }

  @Test
  void deleteExistentStudent() {
    assertDoesNotThrow(() -> studentService.delete("123"));
    ArgumentCaptor<Student> studentCap = ArgumentCaptor.forClass(Student.class);
    verify(studentRepository, times(1)).delete(studentCap.capture());

    Student student = studentCap.getValue();
    assertEquals("123", student.getId());
  }

  @Test
  void deleteNonExistentStudent() {
    assertThrows(ResponseStatusException.class, () -> studentService.delete("nonExistentId"));
  }

}
