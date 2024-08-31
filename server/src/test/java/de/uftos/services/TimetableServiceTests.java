package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.TimetableRequestDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.LessonsCount;
import de.uftos.entities.Room;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
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
    Future<TimetableSolutionDto> solutionFuture = es.submit(() -> timetableSolutionDto);

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
    timetableDataSetup();
    TimetableRequestDto timetableRequestDto = new TimetableRequestDto("timetableRequest");
    assertDoesNotThrow(() -> timetableService.create(timetableRequestDto));

  }

  private void timetableDataSetup() {
    ConstraintInstance constraintInstance = new ConstraintInstance();
    ConstraintSignature constraintSignature = new ConstraintSignature();
    Curriculum curriculum = new Curriculum();
    Grade grade = new Grade("grade");
    Room room = new Room("room");
    Student student = new Student("student");
    StudentGroup studentGroup = new StudentGroup("studentGroup");
    Subject subject = new Subject("subject");
    Tag tag = new Tag("tag");
    Teacher teacher = new Teacher("teacher");
    Timeslot timeslot = new Timeslot("timeslot");

    //TODO: fix null-pointers
    grade.getStudentGroups().add(studentGroup);
    studentGroup.getGrades().add(grade);

    grade.getTags().add(tag);
    tag.getGrades().add(grade);

    grade.setCurriculum(curriculum);
    curriculum.setGrade(grade);

    room.getTags().add(tag);
    tag.getRooms().add(room);

    student.getGroups().add(studentGroup);
    studentGroup.getStudents().add(student);

    student.getTags().add(tag);
    tag.getStudents().add(student);

    studentGroup.getSubjects().add(subject);

    studentGroup.getTags().add(tag);
    tag.getStudentGroups().add(studentGroup);

    subject.getTeachers().add(teacher);
    teacher.getSubjects().add(subject);

    subject.getTags().add(tag);
    tag.getSubjects().add(subject);

    tag.getTeachers().add(teacher);
    teacher.getTags().add(tag);

    tag.getTimeslots().add(timeslot);
    timeslot.getTags().add(tag);

    curriculum.getLessonsCounts().add(new LessonsCount(subject.getId(), 2));

    //TODO: set Values for ConstraintInstance and ConstraintSignature

    constraintSignature.getInstances().add(constraintInstance);

    when(constraintInstanceRepository.findAll()).thenReturn(List.of(constraintInstance));
    when(constraintSignatureRepository.findAll()).thenReturn(List.of(constraintSignature));
    when(curriculumRepository.findAll()).thenReturn(List.of(curriculum));
    when(gradeRepository.findAll()).thenReturn(List.of(grade));
    when(roomRepository.findAll()).thenReturn(List.of(room));
    when(studentRepository.findAll()).thenReturn(List.of(student));
    when(studentGroupRepository.findAll()).thenReturn(List.of(studentGroup));
    when(subjectRepository.findAll()).thenReturn(List.of(subject));
    when(tagRepository.findAll()).thenReturn(List.of(tag));
    when(teacherRepository.findAll()).thenReturn(List.of(teacher));
    when(timeslotRepository.findAll()).thenReturn(List.of(timeslot));
  }
}
