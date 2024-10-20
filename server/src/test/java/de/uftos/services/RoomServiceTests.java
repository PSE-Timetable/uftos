package de.uftos.services;


import static de.uftos.utils.ClassCaster.getClassType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.RoomRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Break;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timetable;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.TimetableRepository;
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
public class RoomServiceTests {
  @Mock
  private RoomRepository roomRepository;

  @Mock
  private ServerRepository serverRepository;

  @Mock
  private ConstraintSignatureRepository signatureRepository;

  @Mock
  private ConstraintInstanceRepository instanceRepository;

  @Mock
  private LessonRepository lessonRepository;

  @Mock
  private TimetableRepository timetableRepository;

  @InjectMocks
  private RoomService roomService;


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
    Room room1 = new Room("100", "10.10", 10, List.of("T1", "T2", "T3"));
    room1.setId("123");

    Room room2 = new Room("101", "11.11", 50, List.of("3", "4", "5"));
    room2.setId("456");
    room2.setLessons(Collections.emptyList());

    Student student1 = new Student("123");
    Student student2 = new Student("345");
    Student student3 = new Student("153");
    Student student4 = new Student("325");


    StudentGroup studentGroup1 = new StudentGroup("654");
    studentGroup1.setStudents(List.of(student1, student2));

    StudentGroup studentGroup2 = new StudentGroup("674");
    studentGroup2.setStudents(List.of(student3, student4));

    Grade grade1 = new Grade("723");
    grade1.setStudentGroups(List.of(studentGroup1, studentGroup2));

    studentGroup1.setGrades(List.of(grade1));
    studentGroup2.setGrades(List.of(grade1));

    Subject subject = new Subject();
    subject.setId("789");

    Teacher teacher1 = new Teacher("123");

    Timetable timetable = new Timetable("timetable");
    timetable.setId("timetableId");

    Lesson lesson1 = createLesson(teacher1, room1, studentGroup1, "2024", subject, timetable);
    Lesson lesson2 = createLesson(teacher1, room1, studentGroup1, "2022", subject, timetable);
    Lesson lesson3 = createLesson(teacher1, room1, studentGroup2, "2024", subject, timetable);

    room1.setLessons(List.of(lesson1, lesson2, lesson3));

    Server server =
        new Server(new TimetableMetadata(45, 8, "7:45", new Break[] {}), "2024", "test@uftos.de");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(roomRepository.findById("123")).thenReturn(Optional.of(room1));
    when(roomRepository.findById("456")).thenReturn(Optional.of(room2));
    when(roomRepository.findAllById(List.of("123"))).thenReturn(List.of(room1));
    when(roomRepository.findAllById(List.of("nonExistentId", "123"))).thenReturn(List.of(room1));

  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> roomService.getById("nonExistentId"));
  }

  @Test
  void createRoom() {
    RoomRequestDto requestDto =
        new RoomRequestDto("roomName", "buildingName", 10, List.of("tagId"));
    roomService.create(requestDto);

    ArgumentCaptor<Room> roomCap = ArgumentCaptor.forClass(Room.class);
    verify(roomRepository, times(1)).save(roomCap.capture());

    Room room = roomCap.getValue();
    assertNotNull(room);
    assertEquals("roomName", room.getName());

    assertEquals("buildingName", room.getBuildingName());

    assertEquals(10, room.getCapacity());

    assertEquals(1, room.getTags().size());
    assertEquals("tagId", room.getTags().getFirst().getId());
  }

  @Test
  void createRoomEmptyName() {
    RoomRequestDto requestDto =
        new RoomRequestDto("", "buildingName", 10, List.of("tagId"));
    assertThrows(ResponseStatusException.class, () -> roomService.create(requestDto));
  }

  @Test
  void createRoomEmptyBuildingName() {
    RoomRequestDto requestDto =
        new RoomRequestDto("roomName", "", 10, List.of("tagId"));
    assertThrows(ResponseStatusException.class, () -> roomService.create(requestDto));
  }


  @Test
  void updateRoom() {
    RoomRequestDto requestDto =
        new RoomRequestDto("newRoomName", "newBuildingName", 11, List.of());
    roomService.update("123", requestDto);

    ArgumentCaptor<Room> roomCap = ArgumentCaptor.forClass(Room.class);
    verify(roomRepository, times(1)).save(roomCap.capture());

    Room room = roomCap.getValue();
    assertNotNull(room);
    assertEquals("newRoomName", room.getName());

    assertEquals("newBuildingName", room.getBuildingName());

    assertEquals(11, room.getCapacity());

    assertEquals(0, room.getTags().size());
  }

  @Test
  void updateRoomEmptyName() {
    RoomRequestDto requestDto =
        new RoomRequestDto("", "buildingName", 10, List.of("tagId"));
    assertThrows(ResponseStatusException.class, () -> roomService.update("123", requestDto));
  }

  @Test
  void updateRoomEmptyBuildingName() {
    RoomRequestDto requestDto =
        new RoomRequestDto("roomName", "", 10, List.of("tagId"));
    assertThrows(ResponseStatusException.class, () -> roomService.update("123", requestDto));
  }

  @Test
  void deleteExistingRoom() {
    assertDoesNotThrow(() -> roomService.deleteRooms(new String[] {"123"}));
    ArgumentCaptor<List<Room>> roomCap = ArgumentCaptor.forClass(getClassType());
    verify(roomRepository, times(1)).deleteAll(roomCap.capture());

    List<Room> room = roomCap.getValue();
    assertEquals(1, room.size());
    assertEquals("123", room.getFirst().getId());
  }

  @Test
  void deleteNonExistingRoom() {
    assertThrows(ResponseStatusException.class,
        () -> roomService.deleteRooms(new String[] {"nonExistentId"}));
  }

  @Test
  void deleteRoomsNonExistent() {
    assertThrows(ResponseStatusException.class,
        () -> roomService.deleteRooms(new String[] {"nonExistentId"}));
  }

  @Test
  void deleteRoomsSomeExistent() {
    assertThrows(ResponseStatusException.class,
        () -> roomService.deleteRooms(new String[] {"nonExistentId", "123"}));
  }

  @Test
  void deleteRoomsAllExistent() {
    assertDoesNotThrow(() -> roomService.deleteRooms(new String[] {"123"}));
    Class<List<Room>> listClass =
        (Class<List<Room>>) (Class) List.class;
    ArgumentCaptor<List<Room>> roomCap = ArgumentCaptor.forClass(listClass);
    verify(roomRepository, times(1)).deleteAll(roomCap.capture());

    List<Room> roomList = roomCap.getValue();
    assertEquals(1, roomList.size());
    assertEquals("123", roomList.getFirst().getId());

  }

  @Test
  void emptyLessons() {
    assertDoesNotThrow(() -> roomService.getLessonsById("456"));
    LessonResponseDto result = roomService.getLessonsById("456");
    assertResultArraysSizes(result, 0, 0, 0, 0);
  }

  @Test
  void lessonsById() {
    Room room1 = roomRepository.findById("123").orElseThrow();

    LessonResponseDto result = roomService.getLessonsById("123");
    assertResultArraysSizes(result, 1, 2, 1, 1);
    assertAll("Testing whether the sizes of the arrays are correct",
        () -> assertEquals(2, result.grades().getFirst().studentGroupIds().size()),
        () -> assertEquals(4, result.grades().getFirst().studentIds().size())
    );

    assertAll("Testing whether all the rooms are there",
        () -> assertTrue(
            result.rooms().stream().map(Room::getId).toList().contains(room1.getId()))
    );

    assertAll("Testing whether all the student groups are there",
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("674")),
        () -> assertTrue(result.grades().getFirst().studentGroupIds().contains("654"))
    );

    assertAll("Testing whether all the students are there",
        () -> assertTrue(result.grades().getFirst().studentIds().contains("123")),
        () -> assertTrue(result.grades().getFirst().studentIds().contains("345"))
    );
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
