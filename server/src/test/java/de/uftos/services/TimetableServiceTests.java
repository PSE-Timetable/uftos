package de.uftos.services;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import de.uftos.dto.ResourceType;
import de.uftos.dto.Weekday;
import de.uftos.dto.requestdtos.LessonsCountRequestDto;
import de.uftos.dto.requestdtos.TimetableRequestDto;
import de.uftos.entities.Break;
import de.uftos.entities.ConstraintParameter;
import de.uftos.entities.ConstraintSignature;
import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.Timetable;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.repositories.database.TimetableRepository;
import de.uftos.repositories.solver.SolverRepository;
import de.uftos.repositories.solver.SolverRepositoryImpl;
import de.uftos.repositories.ucdl.UcdlRepository;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TimetableServiceTests {

  @Spy
  private final SolverRepository solverRepository = new SolverRepositoryImpl();
  @Mock
  private TimetableRepository timetableRepository;
  @Mock
  private CurriculumRepository curriculumRepository;
  @Mock
  private ConstraintSignatureRepository constraintSignatureRepository;
  @Mock
  private GradeRepository gradeRepository;
  @Mock
  private LessonRepository lessonRepository;
  @Mock
  private RoomRepository roomRepository;
  @Mock
  private StudentGroupRepository studentGroupRepository;
  @Mock
  private StudentRepository studentRepository;
  @Mock
  private SubjectRepository subjectRepository;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private TeacherRepository teacherRepository;
  @Mock
  private TimeslotRepository timeslotRepository;
  @Mock
  private UcdlRepository ucdlRepository;
  @Mock
  private ServerRepository serverRepository;

  @InjectMocks
  private TimetableService timetableService;

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  @BeforeEach
  void setUp() throws ParseException, IOException {
    //creation of new resources
    Lesson lesson1 = new Lesson();
    lesson1.setId("456");

    Lesson lesson2 = new Lesson();
    lesson2.setId("789");

    Student student1 = new Student("student123");
    student1.setTags(new ArrayList<>());
    student1.setGroups(new ArrayList<>());
    student1.setLastName("lastName");
    student1.setFirstName("firstName");

    Grade grade = new Grade("grade123");
    grade.setTags(new ArrayList<>());
    grade.setStudentGroups(new ArrayList<>());
    grade.setName("name");

    StudentGroup studentGroup = new StudentGroup("group123");
    studentGroup.setStudents(new ArrayList<>());
    studentGroup.setGrades(new ArrayList<>());
    studentGroup.setLessons(new ArrayList<>());
    studentGroup.setSubjects(new ArrayList<>());
    studentGroup.setTags(new ArrayList<>());
    studentGroup.setName("name");

    Teacher teacher = new Teacher("teacher123");
    teacher.setTags(new ArrayList<>());
    teacher.setSubjects(new ArrayList<>());
    teacher.setLessons(new ArrayList<>());
    teacher.setFirstName("firstName");
    teacher.setLastName("lastName");
    teacher.setAcronym("acronym");

    Subject subject = new Subject("subject123");
    subject.setTags(new ArrayList<>());
    subject.setLessons(new ArrayList<>());
    subject.setTeachers(new ArrayList<>());
    subject.setLessonsCounts(new ArrayList<>());
    subject.setName("name");
    subject.setColor("color");

    Room room = new Room("room123");
    room.setLessons(new ArrayList<>());
    room.setTags(new ArrayList<>());
    room.setName("name");
    room.setCapacity(0);
    room.setBuildingName("buildingName");

    Timeslot timeslot = new Timeslot("timeslot123");
    timeslot.setTags(new ArrayList<>());
    timeslot.setLessons(new ArrayList<>());
    timeslot.setSlot(0);
    timeslot.setDay(Weekday.MONDAY);

    //relations between resources
    studentGroup.setGrades(List.of(grade));
    grade.setStudentGroups(List.of(studentGroup));

    //timetable
    Timetable timetable = new Timetable("timetable123");
    timetable.setLessons(List.of(lesson1, lesson2));

    LessonsCountRequestDto lessonsCountRequestDto = new LessonsCountRequestDto("subject123", 5);
    Curriculum curriculum = new Curriculum(grade, "Curriculum", List.of(lessonsCountRequestDto));
    grade.setCurriculum(curriculum);

    ConstraintParameter constraintParameter = new ConstraintParameter();
    constraintParameter.setParameterName("teacher_param");
    constraintParameter.setParameterType(ResourceType.TEACHER);

    ConstraintSignature constraintSignature = new ConstraintSignature();
    constraintSignature.setParameters(List.of(constraintParameter));

    when(timetableRepository.findAll()).thenReturn(List.of(timetable));
    when(timetableRepository.findById("timetable123")).thenReturn(Optional.of(timetable));

    when(curriculumRepository.findAll()).thenReturn(List.of(curriculum));
    when(curriculumRepository.findById("curriculum123")).thenReturn(Optional.of(curriculum));

    when(studentGroupRepository.findAll()).thenReturn(List.of(studentGroup));
    when(studentGroupRepository.findById("group123")).thenReturn(Optional.of(studentGroup));

    when(studentRepository.findAll()).thenReturn(List.of(student1));
    when(studentRepository.findById("student123")).thenReturn(Optional.of(student1));

    when(teacherRepository.findAll()).thenReturn(List.of(teacher));
    when(teacherRepository.findById("teacher123")).thenReturn(Optional.of(teacher));

    when(subjectRepository.findAll()).thenReturn(List.of(subject));
    when(subjectRepository.findById("subject123")).thenReturn(Optional.of(subject));

    when(roomRepository.findAll()).thenReturn(List.of(room));
    when(roomRepository.findById("room123")).thenReturn(Optional.of(room));

    when(timeslotRepository.findAll()).thenReturn(List.of(timeslot));
    when(timeslotRepository.findById("timeslot123")).thenReturn(Optional.of(timeslot));

    when(curriculumRepository.findAll()).thenReturn(List.of(curriculum));
    when(curriculumRepository.findById("curriculum123")).thenReturn(Optional.of(curriculum));

    when(curriculumRepository.findAll()).thenReturn(List.of(curriculum));
    when(curriculumRepository.findById("curriculum123")).thenReturn(Optional.of(curriculum));

    when(curriculumRepository.findAll()).thenReturn(List.of(curriculum));
    when(curriculumRepository.findById("curriculum123")).thenReturn(Optional.of(curriculum));

    when(ucdlRepository.getConstraints()).thenReturn(new HashMap<>());

    Break[] breaks = new Break[0];
    Server server = new Server(new TimetableMetadata(45, 10, "07:45", breaks), "2024");
    when(serverRepository.findAll()).thenReturn(List.of(server));
  }

  @Test
  void nonExistingTimetableById() {
    assertThrows(ResponseStatusException.class, () -> timetableService.getById("321"));
  }

  @Test
  void createTimetable() {
    TimetableRequestDto timetable = new TimetableRequestDto("2024");
    assertDoesNotThrow(() -> timetableService.create(timetable));
  }

  @Test
  void timetableById() {
    Timetable timetable = timetableService.getById("timetable123");
    assertEquals("timetable123", timetable.getId());
  }
}
