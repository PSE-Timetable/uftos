package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.CurriculumRequestDto;
import de.uftos.dto.requestdtos.LessonsCountRequestDto;
import de.uftos.dto.responsedtos.CurriculumResponseDto;
import de.uftos.dto.responsedtos.GradeResponseDto;
import de.uftos.entities.Break;
import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.Server;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.ServerRepository;
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


@SuppressWarnings("checkstyle:all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CurriculumServiceTests {
  @Mock
  private final Grade testGrade = new Grade("testName", List.of("group5"),
      List.of("T2", "T3"));
  @Mock
  private final Grade newGrade = new Grade("newGradeName", List.of("group5"),
      List.of("T2", "T3"));
  @Mock
  private GradeRepository gradeRepository;
  @Mock
  private CurriculumRepository curriculumRepository;
  @Mock
  private ServerRepository serverRepository;
  /*
   the grades need to be mocked to return a studentGroup with a non-null
   student list, when getStudentGroups() is called.
   Otherwise a null pointer exception is thrown when making a
   response dto from the curricula with these grades.
  */
  @InjectMocks
  private CurriculumService curriculumService;


  @BeforeEach
  void setUp() {
    Grade grade1 = new Grade("5", List.of("group1", "group2"),
        List.of("T1", "T2"));
    Grade grade2 = new Grade("7", List.of("group3", "group4"),
        List.of("T2", "T3"));

    newGrade.setId("newGrade");

    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Mathe", 4);
    LessonsCountRequestDto otherLessonCount = new LessonsCountRequestDto("OtherSubject", 4);

    Curriculum curriculum1 = new Curriculum(testGrade, "EmptyCurriculum", List.of());
    curriculum1.setId("123");
    Curriculum curriculum2 = new Curriculum(grade2, "TestCurriculum", List.of(lessonsCount));
    curriculum2.setId("456");
    Curriculum curriculum3 = new Curriculum(newGrade, "UpdateCurriculum", List.of(lessonsCount));
    curriculum3.setId("234");
    Curriculum curriculumMultipleLessonsCounts =
        new Curriculum(newGrade, "UpdateCurriculum", List.of(lessonsCount, otherLessonCount));
    curriculumMultipleLessonsCounts.setId("multipleCounts");

    Curriculum testCurriculum =
        new Curriculum(testGrade, "testName", List.of(lessonsCount));

    StudentGroup group5 = new StudentGroup("group5", List.of(), List.of(), List.of());

    Server server =
        new Server(new TimetableMetadata(45, 8, "7:45", new Break[] {}), "2024", "test@uftos.de");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(testGrade.getStudentGroups()).thenReturn(List.of(group5));
    when(gradeRepository.findById("testGrade")).thenReturn(Optional.of(testGrade));
    when(gradeRepository.findById("newGrade")).thenReturn(Optional.of(newGrade));
    when(curriculumRepository.findById("123")).thenReturn(Optional.of(curriculum1));
    when(curriculumRepository.findById("456")).thenReturn(Optional.of(curriculum2));
    when(curriculumRepository.findById("234")).thenReturn(Optional.of(curriculum3));
    when(curriculumRepository.findById("multipleCounts")).thenReturn(
        Optional.of(curriculumMultipleLessonsCounts));
    when(curriculumRepository.save(any(Curriculum.class))).thenReturn(testCurriculum);
  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> curriculumService.getById("nonExistentId"));
  }

  @Test
  void getByExistentId() {
    assertDoesNotThrow(() -> curriculumService.getById("123"));
    CurriculumResponseDto result = curriculumService.getById("123");

    assertAll("Testing whether the response dto is correct",
        () -> assertEquals("123", result.id()),
        () -> assertEquals("EmptyCurriculum", result.name()),
        () -> assertEquals(0, result.lessonsCounts().size()),
        () -> assertEquals(GradeResponseDto.createResponseDtoFromGrade(testGrade), result.grade())
    );
  }

  @Test
  void createCurriculum() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Englisch", 4);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("testGrade", "testName", List.of(lessonsCount));
    curriculumService.create(requestDto);

    ArgumentCaptor<Curriculum> curriculumCap = ArgumentCaptor.forClass(Curriculum.class);
    verify(curriculumRepository, times(1)).save(curriculumCap.capture());

    Curriculum curriculum = curriculumCap.getValue();
    assertNotNull(curriculum);

    assertEquals(testGrade, curriculum.getGrade());

    assertEquals("testName", curriculum.getName());

    assertEquals(1, curriculum.getLessonsCounts().size());
  }

  @Test
  void createEmptyName() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Englisch", 4);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("testGrade", "", List.of(lessonsCount));
    assertThrows(ResponseStatusException.class, () -> curriculumService.create(requestDto));
  }

  @Test
  void createNonExistentGrade() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Englisch", 4);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("nonExistentGrade", "testName", List.of(lessonsCount));
    assertThrows(ResponseStatusException.class, () -> curriculumService.create(requestDto));
  }

  @Test
  void updateCurriculum() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Mathe", 5);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("newGrade", "newName", List.of(lessonsCount));
    curriculumService.update("234", requestDto);

    Curriculum curriculum = curriculumRepository.findById("234").get();
    assertNotNull(curriculum);

    assertEquals(newGrade, curriculum.getGrade());

    assertEquals(1, curriculum.getLessonsCounts().size());
  }

  @Test
  void updateCurriculumMultipleLessonsCounts() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Mathe", 5);
    LessonsCountRequestDto otherCount = new LessonsCountRequestDto("OtherSubject", 5);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("newGrade", "newName", List.of(lessonsCount, otherCount));
    curriculumService.update("multipleCounts", requestDto);

    Curriculum curriculum = curriculumRepository.findById("multipleCounts").get();
    assertNotNull(curriculum);

    assertEquals(newGrade, curriculum.getGrade());

    assertEquals(2, curriculum.getLessonsCounts().size());
  }

  @Test
  void updateCurriculumInconsistentLessonCountAmounts() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Mathe", 5);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("newGrade", "newName", List.of(lessonsCount));
    assertThrows(ResponseStatusException.class, () -> curriculumService.update("123", requestDto));
  }

  @Test
  void updateCurriculumInconsistentLessonCountSubjects() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("OtherSubject", 5);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("newGrade", "newName", List.of(lessonsCount));
    assertThrows(ResponseStatusException.class, () -> curriculumService.update("234", requestDto));
  }

  @Test
  void updateCurriculumNonExistent() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Physik", 4);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("newGrade", "newName", List.of(lessonsCount));
    assertThrows(ResponseStatusException.class,
        () -> curriculumService.update("nonExistentId", requestDto));
  }

  @Test
  void updateCurriculumNonExistentGrade() {
    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("Physik", 4);
    CurriculumRequestDto requestDto =
        new CurriculumRequestDto("nonExistentGrade", "newName", List.of(lessonsCount));
    assertThrows(ResponseStatusException.class,
        () -> curriculumService.update("234", requestDto));
  }

  @Test
  void deleteExistentCurriculum() {
    assertDoesNotThrow(() -> curriculumService.delete("123"));
    ArgumentCaptor<Curriculum> curriculumCap = ArgumentCaptor.forClass(Curriculum.class);
    verify(curriculumRepository, times(1)).delete(curriculumCap.capture());

    Curriculum curriculum = curriculumCap.getValue();
    assertEquals("123", curriculum.getId());
  }

  @Test
  void deleteNonExistentCurriculum() {
    assertThrows(ResponseStatusException.class, () -> curriculumService.delete("nonExistentId"));
  }

}
