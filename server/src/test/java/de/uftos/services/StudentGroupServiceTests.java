package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.Break;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudentGroupServiceTests {
  @Mock
  private StudentGroupRepository studentGroupRepository;

  @Mock
  private ServerRepository serverRepository;

  @InjectMocks
  private StudentGroupService studentGroupService;

  private Room room1;
  private Room room2;

  private static void assertResultArraySizes(LessonResponseDto result, int teachers, int lessons,
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
    StudentGroup studentGroup1 =
        new StudentGroup("Ethik", List.of("S1", "S2"), List.of(), List.of());
    studentGroup1.setId("123");

    StudentGroup studentGroup2 =
        new StudentGroup("Religion", List.of("S2", "S3"), List.of(), List.of("T1", "T2"));
    studentGroup2.setId("456");
    studentGroup2.setLessons(Collections.emptyList());


    Grade grade1 = new Grade("723");
    grade1.setStudentGroups(List.of(studentGroup1, studentGroup2));
    studentGroup1.setGrades(List.of(grade1));
    studentGroup2.setGrades(List.of(grade1));

    Subject subject = new Subject("789");

    room1 = new Room("534");
    room2 = new Room("574");

    Teacher teacher1 = new Teacher("T1");
    Teacher teacher2 = new Teacher("T2");

    Lesson lesson1 = createLesson(teacher1, room1, studentGroup1, "2024", subject);
    Lesson lesson2 = createLesson(teacher2, room1, studentGroup1, "2022", subject);
    Lesson lesson3 = createLesson(teacher1, room2, studentGroup2, "2024", subject);

    studentGroup1.setLessons(List.of(lesson1, lesson2, lesson3));

    Break[] breaks = {};
    Server server = new Server(new TimetableMetadata(45, "8:00", breaks), "2024");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(studentGroupRepository.findById("123")).thenReturn(Optional.of(studentGroup1));
    when(studentGroupRepository.findById("456")).thenReturn(Optional.of(studentGroup2));
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
        () -> assertEquals(2, result.grades().getFirst().studentGroupIds().size()),
        () -> assertEquals(3, result.grades().getFirst().studentIds().size())
    );

    assertAll("Testing whether all the rooms are there",
        () -> assertTrue(result.rooms().contains(room1)),
        () -> assertTrue(result.rooms().contains(room2))
    );

    assertAll("Testing whether all the student groups are there",
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("123")),
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("456"))
    );

    assertAll("Testing whether all the students are there",
        () -> assertTrue(result.grades().getFirst().studentIds().contains("S1")),
        () -> assertTrue(result.grades().getFirst().studentIds().contains("S2")),
        () -> assertTrue(result.grades().getFirst().studentIds().contains("S3"))
    );
  }

  private Lesson createLesson(Teacher teacher, Room room, StudentGroup studentGroup,
                              String number,
                              Subject subject) {
    Lesson lesson = new Lesson();
    lesson.setTeacher(teacher);
    lesson.setRoom(room);
    lesson.setStudentGroup(studentGroup);
    lesson.setYear(number);
    lesson.setSubject(subject);
    return lesson;
  }


}
