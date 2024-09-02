package de.uftos.services;

import static de.uftos.utils.ClassCaster.getClassType;
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

import de.uftos.dto.SuccessResponse;
import de.uftos.dto.requestdtos.GradeRequestDto;
import de.uftos.dto.responsedtos.GradeResponseDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Break;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timetable;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.SubjectRepository;
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
public class GradeServiceTests {
  private final GradeRequestDto requestDtoForCreateAndUpdate =
      new GradeRequestDto("testName", List.of(), List.of("tagId"));

  private final GradeRequestDto requestDtoForCreateAndUpdateEmptyName =
      new GradeRequestDto("", List.of(), List.of("tagId"));

  @Mock
  private GradeRepository gradeRepository;

  @Mock
  private ServerRepository serverRepository;

  @Mock
  private StudentGroupRepository studentGroupRepository;

  @Mock
  private ConstraintSignatureRepository signatureRepository;

  @Mock
  private ConstraintInstanceRepository instanceRepository;

  @Mock
  private SubjectRepository subjectRepository;

  @Mock
  private CurriculumRepository curriculumRepository;

  @Mock
  private Grade grade1Mock;

  @Mock
  private Grade grade2Mock;

  @Mock
  private Grade grade3Mock;

  @Mock
  private Grade gradeForCreateAndUpdateMock;

  @InjectMocks
  private GradeService gradeService;

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
    StudentGroup studentGroup1 =
        new StudentGroup("5-Ethics", List.of("S1", "S2"), List.of(), List.of());
    studentGroup1.setId("g123");

    StudentGroup studentGroup2 =
        new StudentGroup("5-Religion", List.of("S2", "S3"), List.of(),
            List.of("T1", "T2"));
    studentGroup2.setId("g456");

    StudentGroup studentGroup3 =
        new StudentGroup("7-Ethics", List.of("S1", "S2"), List.of(), List.of());
    studentGroup3.setId("g234");

    StudentGroup studentGroup4 =
        new StudentGroup("7-Religion", List.of("S2", "S3"), List.of(),
            List.of("T1", "T2"));
    studentGroup4.setId("g567");

    StudentGroup studentGroup5 =
        new StudentGroup("7b-Religion", List.of("S2", "S3"), List.of(),
            List.of("T1", "T2"));
    studentGroup5.setId("g678");


    room1 = new Room("534");
    room2 = new Room("574");

    Subject subject = new Subject("789");

    Teacher teacher1 = new Teacher("Te1");
    Teacher teacher2 = new Teacher("Te2");

    Timetable timetable = new Timetable("timetable");
    timetable.setId("timetableId");

    Lesson lesson1 = createLesson(teacher1, room1, studentGroup1, "2024", subject, timetable);
    lesson1.setId("l1");
    Lesson lesson2 = createLesson(teacher2, room1, studentGroup1, "2022", subject, timetable);
    lesson2.setId("l2");
    Lesson lesson3 = createLesson(teacher1, room2, studentGroup1, "2024", subject, timetable);
    lesson3.setId("l3");
    Lesson lesson4 = createLesson(teacher1, room2, studentGroup2, "2022", subject, timetable);
    lesson4.setId("l4");
    Lesson lesson5 = createLesson(teacher2, room2, studentGroup2, "2024", subject, timetable);
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

    Grade grade3 = new Grade("7", List.of(),
        List.of("T2", "T3"));
    grade2.setId("567");


    studentGroup1.setGrades(List.of(grade1Mock));
    studentGroup2.setGrades(List.of(grade1Mock));

    when(subjectRepository.findAll()).thenReturn(List.of(subject));

    Break[] breaks = {};
    Server server =
        new Server(new TimetableMetadata(45, 8, "8:00", breaks), "2024", "test@uftos.de");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(studentGroupRepository.findAllByGrades(List.of("123"))).thenReturn(
        List.of(studentGroup1, studentGroup2));
    when(gradeRepository.findById("123")).thenReturn(Optional.of(grade1Mock));
    when(gradeRepository.findById("456")).thenReturn(Optional.of(grade2Mock));
    when(gradeRepository.findById("567")).thenReturn(Optional.of(grade3Mock));
    when(gradeRepository.save(any(Grade.class))).thenReturn(gradeForCreateAndUpdateMock);
    when(gradeRepository.findAllById(List.of("123"))).thenReturn(List.of(grade1Mock));
    when(gradeRepository.findAllById(List.of("567"))).thenReturn(List.of(grade3Mock));
    when(gradeRepository.findAllById(List.of("nonExistentId", "123"))).thenReturn(
        List.of(grade1Mock));
    when(grade1Mock.getStudentGroups()).thenReturn(List.of(studentGroup1, studentGroup2));
    when(grade1Mock.getId()).thenReturn(grade1.getId());
    when(grade1Mock.getName()).thenReturn(grade1.getName());
    when(grade1Mock.getTags()).thenReturn(grade1.getTags());
    when(grade2Mock.getStudentGroups()).thenReturn(List.of(studentGroup3, studentGroup4));
    when(grade3Mock.getId()).thenReturn("567");

    Grade gradeForTests = requestDtoForCreateAndUpdate.map();
    when(gradeForCreateAndUpdateMock.getStudentGroups()).thenReturn(
        gradeForTests.getStudentGroups());
    when(gradeForCreateAndUpdateMock.getId()).thenReturn(gradeForTests.getId());
    when(gradeForCreateAndUpdateMock.getName()).thenReturn(gradeForTests.getName());
    when(gradeForCreateAndUpdateMock.getTags()).thenReturn(gradeForTests.getTags());
  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> gradeService.getById("nonExistentId"));
  }

  @Test
  void getByExistentId() {
    assertDoesNotThrow(() -> gradeService.getById("123"));
    GradeResponseDto result = gradeService.getById("123");

    assertAll("Testing whether the response dto is correct",
        () -> assertEquals("123", result.id()),
        () -> assertEquals("5", result.name()),
        () -> assertEquals(2, result.studentGroupIds().size()),
        () -> assertEquals(2, result.tags().size()),
        () -> assertTrue(result.studentGroupIds().contains("g123")),
        () -> assertTrue(result.studentGroupIds().contains("g456"))
    );
  }


  @Test
  void createGrade() {
    gradeService.create(requestDtoForCreateAndUpdate);
    ArgumentCaptor<Grade> gradeCap = ArgumentCaptor.forClass(Grade.class);
    verify(gradeRepository, times(1)).save(gradeCap.capture());

    Grade grade = gradeCap.getValue();
    assertNotNull(grade);
    assertEquals("testName", grade.getName());

    assertEquals(0, grade.getStudentGroups().size());

    assertEquals(1, grade.getTags().size());
    assertEquals("tagId", grade.getTags().getFirst().getId());
  }

  @Test
  void createGradeEmptyName() {
    assertThrows(ResponseStatusException.class,
        () -> gradeService.create(requestDtoForCreateAndUpdateEmptyName));
  }

  @Test
  void updateGradeEmptyName() {
    assertThrows(ResponseStatusException.class,
        () -> gradeService.update("123", requestDtoForCreateAndUpdateEmptyName));
  }

  @Test
  void lessonsById() {
    LessonResponseDto result = gradeService.getLessonsById("123");

    //should be lessons 1, 3, 5
    assertResultArraysSizes(result, 2, 3, 2, 1);
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
  void lessonsByNonExistentId() {
    assertThrows(ResponseStatusException.class, () -> gradeService.getLessonsById("nonExistentId"));
  }

  @Test
  void updateGrade() {
    gradeService.update("123", requestDtoForCreateAndUpdate);
    ArgumentCaptor<Grade> gradeCap = ArgumentCaptor.forClass(Grade.class);
    verify(gradeRepository, times(1)).save(gradeCap.capture());

    Grade grade = gradeCap.getValue();
    assertNotNull(grade);
    assertEquals("testName", grade.getName());

    assertEquals(0, grade.getStudentGroups().size());

    assertEquals(1, grade.getTags().size());
    assertEquals("tagId", grade.getTags().getFirst().getId());
  }

  @Test
  void deleteExistentGrade() {
    SuccessResponse successResponse = gradeService.deleteGrades(new String[] {"567"});
    assertTrue(successResponse.success());
    ArgumentCaptor<List<Grade>> gradeCap = ArgumentCaptor.forClass(getClassType());
    verify(gradeRepository, times(1)).deleteAll(gradeCap.capture());

    List<Grade> grade = gradeCap.getValue();
    assertEquals(1, grade.size());
    assertEquals("567", grade.getFirst().getId());
  }

  @Test
  void deleteGradeAssociatedWithGroup() {
    SuccessResponse response = gradeService.deleteGrades(new String[] {"123"});
    assertFalse(response.success());
  }

  @Test
  void deleteNonExistentGrade() {
    SuccessResponse successResponse = gradeService.deleteGrades(new String[] {"nonExistentId"});
    assertFalse(successResponse.success());
  }

  @Test
  void deleteGradesSomeExistent() {
    SuccessResponse successResponse =
        gradeService.deleteGrades(new String[] {"nonExistentId", "123"});
    assertFalse(successResponse.success());
  }

  @Test
  void deleteGradesAllExistent() {
    SuccessResponse successResponse = gradeService.deleteGrades(new String[] {"567"});
    assertTrue(successResponse.success());
    ArgumentCaptor<List<Grade>> gradeCap = ArgumentCaptor.forClass(getClassType());
    verify(gradeRepository, times(1)).deleteAll(gradeCap.capture());

    List<Grade> gradeList = gradeCap.getValue();
    assertEquals(1, gradeList.size());
    assertEquals("567", gradeList.getFirst().getId());
  }

  @Test
  void emptyLessons() {
    assertDoesNotThrow(() -> gradeService.getLessonsById("456"));
    LessonResponseDto result = gradeService.getLessonsById("456");
    assertResultArraysSizes(result, 0, 0, 0, 0);
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
