package de.uftos.services;

import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.*;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GradeServiceTests {
  @Mock
  private GradeRepository gradeRepository;

  @Mock
  private ServerRepository serverRepository;

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
    studentGroup4.setId("g678");


    room1 = new Room("534");
    room2 = new Room("574");

    Subject subject = new Subject("789");

    Teacher teacher1 = new Teacher("Te1");
    Teacher teacher2 = new Teacher("Te2");

    Lesson lesson1 = createLesson(teacher1, room1, studentGroup1, "2024", subject);
    Lesson lesson2 = createLesson(teacher2, room1, studentGroup1, "2022", subject);
    Lesson lesson3 = createLesson(teacher1, room2, studentGroup2, "2024", subject);
    Lesson lesson4 = createLesson(teacher1, room2, studentGroup2, "2022", subject);
    Lesson lesson5 = createLesson(teacher1, room2, studentGroup2, "2024", subject);

    studentGroup1.setLessons(List.of(lesson1, lesson2, lesson3));



    Grade grade1 = new Grade("5", List.of(studentGroup1.getName(), studentGroup2.getName()),
        List.of("T1", "T2"));
    grade1.setId("123");
    studentGroup1.setGrades(List.of(grade1));
    studentGroup2.setGrades(List.of(grade1));
    //TODO consistency check between student groups and grades? If grade has studentGroup, student group should also have grade and vice versa
    Grade grade2 = new Grade("7", List.of(studentGroup3.getName(), studentGroup4.getName()),
        List.of("T2", "T3"));
    grade2.setId("456");
    Grade grade3 = new Grade("7b", List.of(studentGroup1.getName(), studentGroup5.getName()),
        List.of("T2", "T3"));
    grade3.setId("567");





    // TODO lesson constructor with student group array as parameter
    // TODO inconsistency in Grade entity when the corresponding student groups change
    // check if grade entity creates new studentgroups even when group with the same id already exists


    studentGroup2.setLessons(List.of(lesson4, lesson5));
    studentGroup3.setLessons(List.of());
    studentGroup4.setLessons(List.of());
    studentGroup5.setLessons(List.of(lesson3, lesson4, lesson5));

    Server server = new Server(45, "2024");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(gradeRepository.findById("123")).thenReturn(Optional.of(grade1));
    when(gradeRepository.findById("456")).thenReturn(Optional.of(grade2));
    when(gradeRepository.findById("567")).thenReturn(Optional.of(grade3));
  }

  @Test
  void emptyLessons() {
    assertDoesNotThrow(() -> gradeService.getLessonsById("456"));
    LessonResponseDto result = gradeService.getLessonsById("456");
    assertResulArraysSizes(result, 0, 0, 0, 0);
  }

  @Test
  void lessonsById() {
    Grade grade2 = gradeRepository.findById("123").orElseThrow();
    System.out.println(grade2.getStudentGroups());
    LessonResponseDto result = gradeService.getLessonsById("123");
    System.out.println("ResultDto: " + result.toString());
    //should be lessons 1-5
    assertResulArraysSizes(result, 2, 5, 2, 1);
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

  @Test
  void lessonsByIdWithDuplicatesInStudentGroups() {
    LessonResponseDto result = gradeService.getLessonsById("123");
    //should be lessons 1-5, lesson3 is in two student groups
    assertResulArraysSizes(result, 2, 5, 2, 1);
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
