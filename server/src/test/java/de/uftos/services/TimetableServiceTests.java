package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import de.uftos.entities.Break;
import de.uftos.entities.Lesson;
import de.uftos.entities.Server;
import de.uftos.entities.Timetable;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.TimetableRepository;
import de.uftos.repositories.ucdl.UcdlRepository;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.IOException;
import java.util.HashMap;
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
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TimetableServiceTests {

  @Mock
  private TimetableRepository timetableRepository;
  @Mock
  private UcdlRepository ucdlRepository;

  @Mock
  private ServerRepository serverRepository;

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

    when(timetableRepository.findAll()).thenReturn(List.of(timetable));
    when(timetableRepository.findById("timetable123")).thenReturn(Optional.of(timetable));

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
  void getTimetableById() {
    Timetable timetable = timetableService.getById("timetable123");
    assertEquals("timetable123", timetable.getId());
  }
}
