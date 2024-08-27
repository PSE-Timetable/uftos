package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.TimetableRequestDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import de.uftos.entities.Lesson;
import de.uftos.entities.Timetable;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.repositories.database.TimetableRepository;
import de.uftos.repositories.notifications.NotificationRepository;
import de.uftos.repositories.solver.SolverRepository;
import de.uftos.repositories.ucdl.UcdlRepository;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TimetableServiceTests {

  @Mock
  private TimetableRepository timetableRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private SolverRepository solverRepository;

  @Mock
  private GradeRepository gradeRepository;

  @Mock
  private StudentGroupRepository studentGroupRepository;

  @Mock
  private RoomRepository roomRepository;

  @Mock
  private SubjectRepository subjectRepository;

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private CurriculumRepository curriculumRepository;

  @Mock
  private LessonRepository lessonRepository;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private TimeslotRepository timeslotRepository;

  @Mock
  private UcdlRepository ucdlRepository;

  @Mock
  private ConstraintSignatureRepository constraintSignatureRepository;

  @Mock
  private ConstraintInstanceRepository constraintInstanceRepository;

  @InjectMocks
  private TimetableService timetableService;

  @BeforeEach
  void setUp() throws ParseException, IOException {
    Lesson lesson1 = new Lesson();
    lesson1.setId("456");

    Lesson lesson2 = new Lesson();
    lesson2.setId("789");

    Timetable timetable = new Timetable("timetable123");
    timetable.setId("timetable123");
    timetable.setLessons(List.of(lesson1, lesson2));

    TimetableSolutionDto timetableSolutionDto = new TimetableSolutionDto(new ArrayList<>(), 0, 0);

    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
    ExecutorService es = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue);
    Future<TimetableSolutionDto> solutionFuture = es.submit(() -> {
      return timetableSolutionDto;
    });

    when(timetableRepository.findAll()).thenReturn(List.of(timetable));
    when(timetableRepository.findById("timetable123")).thenReturn(Optional.of(timetable));
    when(solverRepository.solve(any(TimetableProblemDto.class), any(Consumer.class))).thenReturn(
        solutionFuture);
  }

  @Test
  void nonExistingTimetableById() {
    assertThrows(ResponseStatusException.class, () -> timetableService.getById("321"));
  }

  @Test
  void getTimetableById() {
    Timetable timetable = timetableService.getById("timetable123");
    assertEquals("timetable123", timetable.getId());
  }

  @Test
  void createTimetable() {
    TimetableRequestDto timetableRequestDto = new TimetableRequestDto("timetableRequest");
    assertDoesNotThrow(() -> timetableService.create(timetableRequestDto));
  }
}
