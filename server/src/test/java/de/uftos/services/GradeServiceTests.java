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
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.ServerRepository;
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
public class GradeServiceTests {
  @Mock
  private GradeRepository gradeRepository;

  @Mock
  private ServerRepository serverRepository;

  @Mock
  private Grade grade1Mock;

  @Mock
  private Grade grade2Mock;


  @InjectMocks
  private GradeService gradeService;

  private Room room1;
  private Room room2;

  private static void assertResulArraysSizes(LessonResponseDto result, int teachers, int lessons,
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
        new StudentGroup("5-Ethik", List.of("S1", "S2"), List.of(), List.of());
    studentGroup1.setId("g123");

    StudentGroup studentGroup2 =
        new StudentGroup("5-Religion", List.of("S2", "S3"), List.of(), List.of("T1", "T2"));
    studentGroup2.setId("g456");

    StudentGroup studentGroup3 =
        new StudentGroup("7-Ethik", List.of("S1", "S2"), List.of(), List.of());
    studentGroup3.setId("g234");

    StudentGroup studentGroup4 =
        new StudentGroup("7-Religion", List.of("S2", "S3"), List.of(), List.of("T1", "T2"));
    studentGroup4.setId("g567");

    StudentGroup studentGroup5 =
        new StudentGroup("7b-Religion", List.of("S2", "S3"), List.of(), List.of("T1", "T2"));
    studentGroup5.setId("g678");


    room1 = new Room("534");
    room2 = new Room("574");

    Subject subject = new Subject("789");

    Teacher teacher1 = new Teacher("Te1");
    Teacher teacher2 = new Teacher("Te2");

    Lesson lesson1 = createLesson(teacher1, room1, studentGroup1, "2024", subject);
    lesson1.setId("l1");
    Lesson lesson2 = createLesson(teacher2, room1, studentGroup1, "2022", subject);
    lesson2.setId("l2");
    Lesson lesson3 = createLesson(teacher1, room2, studentGroup1, "2024", subject);
    lesson3.setId("l3");
    Lesson lesson4 = createLesson(teacher1, room2, studentGroup2, "2022", subject);
    lesson4.setId("l4");
    Lesson lesson5 = createLesson(teacher2, room2, studentGroup2, "2024", subject);
    lesson5.setId("l5");

    studentGroup1.setLessons(List.of(lesson1, lesson2, lesson3));
    studentGroup2.setLessons(List.of(lesson4, lesson5));
    studentGroup3.setLessons(List.of());
    studentGroup4.setLessons(List.of());
    studentGroup5.setLessons(List.of(lesson3, lesson4));

    Grade grade1 = new Grade("5", List.of(studentGroup1.getId(), studentGroup2.getId()),
        List.of("T1", "T2"));
    grade1.setId("123");

    Grade grade2 = new Grade("7", List.of(studentGroup3.getName(), studentGroup4.getName()),
        List.of("T2", "T3"));
    grade2.setId("456");


    studentGroup1.setGrades(List.of(grade1Mock));
    studentGroup2.setGrades(List.of(grade1Mock));

    Break[] breaks = {};
    Server server = new Server(new TimetableMetadata(45, "8:00", breaks), "2024");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(gradeRepository.findById("123")).thenReturn(Optional.of(grade1Mock));
    when(gradeRepository.findById("456")).thenReturn(Optional.of(grade2Mock));
    when(grade1Mock.getStudentGroups()).thenReturn(List.of(studentGroup1, studentGroup2));
    when(grade1Mock.getId()).thenReturn(grade1.getId());
    when(grade1Mock.getName()).thenReturn(grade1.getName());
    when(grade1Mock.getTags()).thenReturn(grade1.getTags());
    when(grade2Mock.getStudentGroups()).thenReturn(List.of(studentGroup3, studentGroup4));
  }


  @Test
  void emptyLessons() {
    assertDoesNotThrow(() -> gradeService.getLessonsById("456"));
    LessonResponseDto result = gradeService.getLessonsById("456");
    assertResulArraysSizes(result, 0, 0, 0, 0);
  }

  @Test
  void lessonsById() {
    LessonResponseDto result = gradeService.getLessonsById("123");

    //should be lessons 1, 3, 5
    assertResulArraysSizes(result, 2, 3, 2, 1);
    assertAll("Testing whether the sizes of the arrays are correct",
        () -> assertEquals(2, result.grades().getFirst().studentGroupIds().size()),
        () -> assertEquals(3, result.grades().getFirst().studentIds().size())
    );

    assertAll("Testing whether all the rooms are there",
        () -> assertTrue(result.rooms().contains(room1)),
        () -> assertTrue(result.rooms().contains(room2))
    );

    assertAll("Testing whether all the student groups are there",
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("g123")),
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("g456"))
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
