package de.uftos.services;

import static de.uftos.utils.ClassCaster.getClassType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.TagRequestDto;
import de.uftos.entities.Break;
import de.uftos.entities.Grade;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import java.util.ArrayList;
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
public class TagServiceTests {
  @Mock
  private TagRepository tagRepository;

  @Mock
  private ServerRepository serverRepository;

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private StudentGroupRepository studentGroupRepository;

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private RoomRepository roomRepository;

  @Mock
  private SubjectRepository subjectRepository;

  @Mock
  private GradeRepository gradeRepository;

  @Mock
  private TimeslotRepository timeslotRepository;

  @Mock
  private ConstraintSignatureRepository signatureRepository;

  @Mock
  private ConstraintInstanceRepository instanceRepository;

  @InjectMocks
  private TagService tagService;

  @BeforeEach
  void setUp() {
    Tag tag = new Tag("", "Visual Impairment");
    tag.setId("123");

    Server server =
        new Server(new TimetableMetadata(45, 8, "7:45", new Break[] {}), "2024", "test@uftos.de");
    when(serverRepository.findAll()).thenReturn(new ArrayList<>(List.of(server)));
    when(tagRepository.findById("123")).thenReturn(Optional.of(tag));
    when(tagRepository.findAllById(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(tag)));

    Teacher teacherWithTag = new Teacher("teacherId");
    teacherWithTag.setTags(new ArrayList<>(List.of(tag)));
    when(teacherRepository.findAllByTags(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(teacherWithTag)));

    StudentGroup groupWithTag = new StudentGroup("groupId");
    groupWithTag.setTags(new ArrayList<>(List.of(tag)));
    when(studentGroupRepository.findAllByTags(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(groupWithTag)));

    Student studentWithTag = new Student("studentId");
    studentWithTag.setTags(new ArrayList<>(List.of(tag)));
    when(studentRepository.findAllByTags(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(studentWithTag)));

    Room roomWithTag = new Room("roomId");
    roomWithTag.setTags(new ArrayList<>(List.of(tag)));
    when(roomRepository.findAllByTags(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(roomWithTag)));

    Subject subjectWithTag = new Subject("subjectId");
    subjectWithTag.setTags(new ArrayList<>(List.of(tag)));
    when(subjectRepository.findAllByTags(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(subjectWithTag)));

    Grade gradeWithTag = new Grade("gradeId");
    gradeWithTag.setTags(new ArrayList<>(List.of(tag)));
    when(gradeRepository.findAllByTags(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(gradeWithTag)));

    Timeslot timeslotWithTag = new Timeslot("timeslotId");
    timeslotWithTag.setTags(new ArrayList<>(List.of(tag)));
    when(timeslotRepository.findAllByTags(new ArrayList<>(List.of("123")))).thenReturn(
        new ArrayList<>(List.of(timeslotWithTag)));
  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> tagService.getById("nonExistentId"));
  }

  @Test
  void getByExistentId() {
    assertDoesNotThrow(() -> tagService.getById("123"));
    Tag result = tagService.getById("123");
    assertNotNull(result);
    assertEquals("123", result.getId());
    assertEquals("Visual Impairment", result.getName());
  }

  @Test
  void createTag() {
    TagRequestDto requestDto =
        new TagRequestDto("Wheelchair");
    tagService.create(requestDto);

    ArgumentCaptor<Tag> tagCap = ArgumentCaptor.forClass(Tag.class);
    verify(tagRepository, times(1)).save(tagCap.capture());

    Tag tag = tagCap.getValue();
    assertNotNull(tag);
    assertEquals("Wheelchair", tag.getName());
  }

  @Test
  void createTagEmptyName() {
    TagRequestDto requestDto =
        new TagRequestDto("");
    assertThrows(ResponseStatusException.class, () -> tagService.create(requestDto));
  }

  @Test
  void updateTagEmptyName() {
    TagRequestDto requestDto =
        new TagRequestDto("");
    assertThrows(ResponseStatusException.class, () -> tagService.update("123", requestDto));
  }

  @Test
  void updateTag() {
    TagRequestDto requestDto =
        new TagRequestDto("newTag");
    tagService.update("123", requestDto);

    ArgumentCaptor<Tag> tagCap = ArgumentCaptor.forClass(Tag.class);
    verify(tagRepository, times(1)).save(tagCap.capture());

    Tag tag = tagCap.getValue();
    assertNotNull(tag);
    assertEquals("newTag", tag.getName());
  }

  @Test
  void deleteExistentTag() {
    assertDoesNotThrow(() -> tagService.deleteTags(new String[] {"123"}));
    ArgumentCaptor<List<Tag>> tagCap = ArgumentCaptor.forClass(getClassType());
    verify(tagRepository, times(1)).deleteAll(tagCap.capture());

    List<Tag> tag = tagCap.getValue();
    assertEquals(1, tag.size());
    assertEquals("123", tag.getFirst().getId());
  }

  @Test
  void deleteNonExistentTag() {
    assertThrows(ResponseStatusException.class,
        () -> tagService.deleteTags(new String[] {"nonExistentId"}));
  }

}
