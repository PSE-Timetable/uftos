package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.responsedtos.ServerStatisticsResponseDto;
import de.uftos.entities.Break;
import de.uftos.entities.Server;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.TeacherRepository;
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
  private StudentGroupRepository studentGroupRepository;

  @Mock
  private RoomRepository roomRepository;

  @InjectMocks
  private ServerService serverService;


  @BeforeEach
  void setUp() {
    Server server =
        new Server(new TimetableMetadata(45, 8, "7:45", new Break[] {}), "2024");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(studentRepository.count()).thenReturn(1L);
    when(teacherRepository.count()).thenReturn(2L);
    when(studentGroupRepository.count()).thenReturn(3L);
    when(roomRepository.count()).thenReturn(4L);
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
  void setMetadata() {
    Break testBreak = new Break(false, 0, 15);
    TimetableMetadata newMetadata = new TimetableMetadata(90, 5, "8:00", new Break[] {testBreak});
    serverService.setTimetableMetadata(newMetadata);

    ArgumentCaptor<Server> serverCap = ArgumentCaptor.forClass(Server.class);
    verify(serverRepository, times(1)).save(serverCap.capture());

    Server server = serverCap.getValue();
    assertEquals(server.getTimetableMetadata(), newMetadata);
    assertEquals(server.getCurrentYear(), "2024");
  }


}
