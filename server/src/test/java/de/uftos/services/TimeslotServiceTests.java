package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.Weekday;
import de.uftos.dto.requestdtos.RoomRequestDto;
import de.uftos.dto.requestdtos.TimeslotRequestDto;
import de.uftos.entities.Break;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.TimeslotRepository;
import java.util.Collections;
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
public class TimeslotServiceTests {
  @Mock
  private TimeslotRepository timeslotRepository;

  @Mock
  private ServerRepository serverRepository;

  @InjectMocks
  private TimeslotService timeslotService;


  @BeforeEach
  void setUp() {
    Timeslot timeslot1 = new Timeslot(Weekday.MONDAY, 0, List.of("tagId"));
    timeslot1.setId("123");

    Server server =
        new Server(new TimetableMetadata(45, 8, "7:45", new Break[] {}), "2024");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(timeslotRepository.findById("123")).thenReturn(Optional.of(timeslot1));
  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> timeslotService.getById("nonExistentId"));
  }

  @Test
  void getByExistentId() {
    assertDoesNotThrow(() -> timeslotService.getById("123"));
    Timeslot result = timeslotService.getById("123");
    assertNotNull(result);
    assertEquals(Weekday.MONDAY, result.getDay());
    assertEquals(0, result.getSlot());
    assertEquals(1, result.getTags().size());
    assertEquals("tagId", result.getTags().getFirst().getId());
  }

  @Test
  void createTimeslot() {
    TimeslotRequestDto requestDto =
        new TimeslotRequestDto(Weekday.TUESDAY, 2, List.of("tagId"));
    timeslotService.create(requestDto);

    ArgumentCaptor<Timeslot> timeslotCap = ArgumentCaptor.forClass(Timeslot.class);
    verify(timeslotRepository, times(1)).save(timeslotCap.capture());

    Timeslot timeslot = timeslotCap.getValue();
    assertNotNull(timeslot);
    assertEquals(Weekday.TUESDAY, timeslot.getDay());

    assertEquals(2, timeslot.getSlot());

    assertEquals(1, timeslot.getTags().size());
    assertEquals("tagId", timeslot.getTags().getFirst().getId());
  }

  @Test
  void updateTimeslot() {
    TimeslotRequestDto requestDto =
        new TimeslotRequestDto(Weekday.WEDNESDAY, 3, List.of("otherTagId"));
    timeslotService.update("123", requestDto);

    ArgumentCaptor<Timeslot> timeslotCap = ArgumentCaptor.forClass(Timeslot.class);
    verify(timeslotRepository, times(1)).save(timeslotCap.capture());

    Timeslot timeslot = timeslotCap.getValue();
    assertNotNull(timeslot);
    assertEquals(Weekday.WEDNESDAY, timeslot.getDay());

    assertEquals(3, timeslot.getSlot());

    assertEquals(1, timeslot.getTags().size());
    assertEquals("otherTagId", timeslot.getTags().getFirst().getId());
  }

  @Test
  void deleteExistentTimeslot() {
    assertDoesNotThrow(() -> timeslotService.delete("123"));
    ArgumentCaptor<Timeslot> timeslotCap = ArgumentCaptor.forClass(Timeslot.class);
    verify(timeslotRepository, times(1)).delete(timeslotCap.capture());

    Timeslot timeslot = timeslotCap.getValue();
    assertEquals("123", timeslot.getId());
  }

  @Test
  void deleteNonExistentTimeslot() {
    assertThrows(ResponseStatusException.class, () -> timeslotService.delete("nonExistentId"));
  }

}
