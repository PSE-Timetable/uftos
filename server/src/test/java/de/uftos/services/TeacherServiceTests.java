package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.TeacherRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
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
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.TeacherRepository;
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
public class TeacherServiceTests {

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private ServerRepository serverRepository;

  @InjectMocks
  private TeacherService teacherService;

  private Room room1;
  private Room room2;

  private static void assertResultArraysSizes(LessonResponseDto result, int teachers, int lessons,
                                              int rooms, int grades) {
    assertAll("Testing whether the sizes of the arrays are correct",
        () -> assertEquals(teachers, result.teachers().size()),
        () -> assertEquals(lessons, result.lessons().size()),
        () -> assertEquals(rooms, result.rooms().size()),
        () -> assertEquals(grades, result.grades().size())
    );
  }

  @BeforeEach
  void setUp() {
    Teacher teacher1 = new Teacher("Max", "Mustermann", "MM",
        List.of("1", "2", "3"), List.of("1", "2", "3"));
    teacher1.setId("123");

    Teacher teacher2 = new Teacher("Alex", "Mustermann", "MA",
        List.of("1", "2", "3"), List.of("1", "2", "3"));
    teacher2.setId("456");
    teacher2.setLessons(Collections.emptyList());

    Student student1 = new Student("123");
    Student student2 = new Student("345");
    Student student3 = new Student("153");
    Student student4 = new Student("325");

    StudentGroup studentGroup1 = new StudentGroup("654");
    studentGroup1.setStudents(List.of(student1, student2));

    StudentGroup studentGroup2 = new StudentGroup("674");
    studentGroup2.setStudents(List.of(student3, student4));

    Grade grade1 = new Grade("723");
    grade1.setStudentGroups(List.of(studentGroup1, studentGroup2));

    studentGroup1.setGrades(List.of(grade1));
    studentGroup2.setGrades(List.of(grade1));

    Subject subject = new Subject();
    subject.setId("789");

    room1 = new Room("534");
    room2 = new Room("574");

    Timetable timetable = new Timetable("timetable");
    timetable.setId("timetableId");

    Lesson lesson1 = createLesson(teacher1, room1, studentGroup1, "2024", subject, timetable);
    Lesson lesson2 = createLesson(teacher1, room1, studentGroup1, "2022", subject, timetable);
    Lesson lesson3 = createLesson(teacher1, room2, studentGroup2, "2024", subject, timetable);

    teacher1.setLessons(List.of(lesson1, lesson2, lesson3));
    teacher1.setSubjects(List.of(subject));

    Server server =
        new Server(new TimetableMetadata(45, 8, "7:45", new Break[] {}), "2024", "test@uftos.de");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(teacherRepository.findById("123")).thenReturn(Optional.of(teacher1));
    when(teacherRepository.findById("456")).thenReturn(Optional.of(teacher2));
  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> teacherService.getById("nonExistentId"));
  }

  @Test
  void createTeacher() {
    TeacherRequestDto requestDto =
        new TeacherRequestDto("firstName", "lastName", "FL", List.of("subjectId"),
            List.of("tagId"));
    teacherService.create(requestDto);

    ArgumentCaptor<Teacher> teacherCap = ArgumentCaptor.forClass(Teacher.class);
    verify(teacherRepository, times(1)).save(teacherCap.capture());

    Teacher teacher = teacherCap.getValue();
    assertNotNull(teacher);
    assertEquals("firstName", teacher.getFirstName());

    assertEquals("lastName", teacher.getLastName());

    assertEquals("FL", teacher.getAcronym());

    assertEquals(1, teacher.getSubjects().size());
    assertEquals("subjectId", teacher.getSubjects().getFirst().getId());

    assertEquals(1, teacher.getTags().size());
    assertEquals("tagId", teacher.getTags().getFirst().getId());
  }

  @Test
  void updateTeacher() {
    TeacherRequestDto requestDto =
        new TeacherRequestDto("newFirstName", "newLastName", "NN", List.of(),
            List.of());
    teacherService.update("123", requestDto);

    ArgumentCaptor<Teacher> teacherCap = ArgumentCaptor.forClass(Teacher.class);
    verify(teacherRepository, times(1)).save(teacherCap.capture());

    Teacher teacher = teacherCap.getValue();
    assertNotNull(teacher);
    assertEquals("newFirstName", teacher.getFirstName());

    assertEquals("newLastName", teacher.getLastName());

    assertEquals("NN", teacher.getAcronym());

    assertEquals(0, teacher.getSubjects().size());

    assertEquals(0, teacher.getTags().size());
  }

  @Test
  void deleteExistingTeacher() {
    assertDoesNotThrow(() -> teacherService.delete("123"));
    ArgumentCaptor<Teacher> teacherCap = ArgumentCaptor.forClass(Teacher.class);
    verify(teacherRepository, times(1)).delete(teacherCap.capture());

    Teacher teacher = teacherCap.getValue();
    assertEquals("123", teacher.getId());
  }

  @Test
  void deleteNonExistingTeacher() {
    assertThrows(ResponseStatusException.class, () -> teacherService.delete("nonExistentId"));
  }

  @Test
  void emptyLessons() {
    assertDoesNotThrow(() -> teacherService.getLessonsById("456"));
    LessonResponseDto result = teacherService.getLessonsById("456");
    assertResultArraysSizes(result, 0, 0, 0, 0);
  }

  @Test
  void lessonsById() {
    LessonResponseDto result = teacherService.getLessonsById("123");
    assertResultArraysSizes(result, 1, 2, 2, 1);
    assertAll("Testing whether the sizes of the arrays are correct",
        () -> assertEquals(2, result.grades().getFirst().studentGroupIds().size()),
        () -> assertEquals(4, result.grades().getFirst().studentIds().size())
    );

    assertAll("Testing whether all the rooms are there",
        () -> assertTrue(
            result.rooms().stream().map(Room::getId).toList().contains(room1.getId())),
        () -> assertTrue(
            result.rooms().stream().map(Room::getId).toList().contains(room2.getId()))
    );

    assertAll("Testing whether all the student groups are there",
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("674")),
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("654"))
    );

    assertAll("Testing whether all the students are there",
        () -> assertTrue(result.grades().getFirst().studentIds().contains("123")),
        () -> assertTrue(result.grades().getFirst().studentIds().contains("345"))
    );
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
}
