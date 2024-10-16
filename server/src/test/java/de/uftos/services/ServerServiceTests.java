package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.Weekday;
import de.uftos.dto.requestdtos.ServerEmailRequestDto;
import de.uftos.dto.responsedtos.ServerEmailResponseDto;
import de.uftos.dto.responsedtos.ServerStatisticsResponseDto;
import de.uftos.entities.Break;
import de.uftos.entities.Server;
import de.uftos.entities.Timeslot;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.repositories.database.TimetableRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ServerServiceTests {

  @Mock
  private ServerRepository serverRepository;

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private GradeRepository gradeRepository;

  @Mock
  private RoomRepository roomRepository;

  @Mock
  private ConstraintSignatureRepository constraintSignatureRepository;

  @Mock
  private TimeslotRepository timeslotRepository;

  @Mock
  private TimetableRepository timetableRepository;

  @Mock
  private LessonRepository lessonRepository;

  @InjectMocks
  private ServerService serverService;


  @BeforeEach
  void setUp() {
    Server server =
        new Server(new TimetableMetadata(45, 8, "7:45", new Break[] {}), "2024", "test@uftos.de");

    List<Timeslot> timeslotList = new ArrayList<>();
    for (Weekday weekday : Weekday.values()) {
      for (int i = 0; i < 8; i++) {
        timeslotList.add(new Timeslot(weekday, i, List.of()));
      }
    }

    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(studentRepository.count()).thenReturn(1L);
    when(teacherRepository.count()).thenReturn(2L);
    when(gradeRepository.count()).thenReturn(3L);
    when(roomRepository.count()).thenReturn(4L);
    when(constraintSignatureRepository.count()).thenReturn(0L);
    when(timeslotRepository.findAll()).thenReturn(timeslotList);
  }

  @Test
  void stats() {
    ServerStatisticsResponseDto result = serverService.getStats();
    assertAll("Testing whether the returned stats are correct",
        () -> assertEquals(1L, result.studentCount()),
        () -> assertEquals(2L, result.teacherCount()),
        () -> assertEquals(3L, result.gradeCount()),
        () -> assertEquals(4L, result.roomCount()),
        () -> assertEquals(0L, result.constraintCount())
    );
  }

  @Test
  void getMetadata() {
    TimetableMetadata result = serverService.getTimetableMetadata();
    assertAll("Testing whether the returned metadata is correct",
        () -> assertEquals(45, result.getTimeslotLength()),
        () -> assertEquals(8, result.getTimeslotsAmount()),
        () -> assertEquals("7:45", result.getStartTime()),
        () -> assertEquals(0, result.getBreaks().length)
    );
  }

  @Test
  void setMetadataFewerTimeslots() {
    Break testBreak = new Break(false, 0, 15);
    TimetableMetadata newMetadata = new TimetableMetadata(90, 5, "8:00", new Break[] {testBreak});
    serverService.setTimetableMetadata(newMetadata);

    ArgumentCaptor<Server> serverCap = ArgumentCaptor.forClass(Server.class);
    verify(serverRepository, times(1)).save(serverCap.capture());

    Server server = serverCap.getValue();
    assertEquals(server.getTimetableMetadata(), newMetadata);
    assertEquals(server.getCurrentYear(), "2024");
  }

  @Test
  void setMetadataSameTimeslotAmount() {
    Break testBreak = new Break(false, 0, 15);
    TimetableMetadata newMetadata = new TimetableMetadata(90, 8, "8:00", new Break[] {testBreak});
    serverService.setTimetableMetadata(newMetadata);

    ArgumentCaptor<Server> serverCap = ArgumentCaptor.forClass(Server.class);
    verify(serverRepository, times(1)).save(serverCap.capture());

    Server server = serverCap.getValue();
    assertEquals(server.getTimetableMetadata(), newMetadata);
    assertEquals(server.getCurrentYear(), "2024");
  }

  @Test
  void setMetadataMoreTimeslots() {
    Break testBreak = new Break(false, 0, 15);
    TimetableMetadata newMetadata = new TimetableMetadata(90, 10, "8:00", new Break[] {testBreak});
    serverService.setTimetableMetadata(newMetadata);

    ArgumentCaptor<Server> serverCap = ArgumentCaptor.forClass(Server.class);
    verify(serverRepository, times(1)).save(serverCap.capture());

    Server server = serverCap.getValue();
    assertEquals(server.getTimetableMetadata(), newMetadata);
    assertEquals(server.getCurrentYear(), "2024");
  }

  @Test
  void getEmail() {
    assertDoesNotThrow(() -> serverService.getEmail());
    assertEquals(new ServerEmailResponseDto("test@uftos.de"), serverService.getEmail());
  }

  @Test
  void setEmail() {
    assertDoesNotThrow(() -> serverService.setEmail(new ServerEmailRequestDto("mail@example.org")));
  }
}
