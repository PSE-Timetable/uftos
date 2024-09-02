package de.uftos.services;

import static de.uftos.utils.ClassCaster.getClassType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.LessonsCountRequestDto;
import de.uftos.dto.requestdtos.SubjectRequestDto;
import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.LessonsCount;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimetableRepository;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SubjectServiceTests {

  private static final String TAG_NAME = "test tag";
  private static final String TAG_ID = "456";
  private static final String SUBJECT_ID = "123";
  private static final String SUBJECT_NAME = "English";

  @Mock
  private SubjectRepository subjectRepository;

  @Mock
  private CurriculumRepository curriculumRepository;

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private StudentGroupRepository studentGroupRepository;

  @Mock
  private ConstraintSignatureRepository signatureRepository;

  @Mock
  private ConstraintInstanceRepository instanceRepository;

  @Mock
  private LessonRepository lessonRepository;

  @Mock
  private TimetableRepository timetableRepository;

  @InjectMocks
  private SubjectService subjectService;

  @BeforeEach
  void setUp() {
    Tag tag = new Tag();
    tag.setId(TAG_ID);
    tag.setName(TAG_NAME);

    Subject subject = new Subject();
    subject.setId(SUBJECT_ID);
    subject.setName(SUBJECT_NAME);
    subject.setTags(new ArrayList<>(List.of(tag)));

    when(subjectRepository.findAll()).thenReturn(new ArrayList<>(List.of(subject)));
    when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
    when(subjectRepository.findAllById(new ArrayList<>(List.of(SUBJECT_ID)))).thenReturn(
        new ArrayList<>(List.of(subject)));
    when(subjectRepository.findAllById(
        new ArrayList<>(List.of("nonExistentId", SUBJECT_ID)))).thenReturn(
        new ArrayList<>(List.of(subject)));
    when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

    LessonsCountRequestDto lessonsCount = new LessonsCountRequestDto("123", 1);
    Curriculum testCurriculum =
        new Curriculum(new Grade("gradeId"), "name", new ArrayList<>(List.of(lessonsCount)));
    when(curriculumRepository.findAll()).thenReturn(new ArrayList<>(List.of(testCurriculum)));
    when(curriculumRepository.findAll(any(Specification.class))).thenReturn(
        new ArrayList<>(List.of(testCurriculum)));

    Teacher teacherWithSubject = new Teacher("teacherId");
    teacherWithSubject.setSubjects(new ArrayList<>(List.of(subject)));
    when(teacherRepository.findAll(any(Specification.class))).thenReturn(
        new ArrayList<>(List.of(teacherWithSubject)));
    when(teacherRepository.findBySubjects(any(Subject.class))).thenReturn(Collections.emptyList());

    StudentGroup groupWithSubject = new StudentGroup("groupId");
    groupWithSubject.setSubjects(new ArrayList<>(List.of(subject)));
    when(studentGroupRepository.findAll(any(Specification.class))).thenReturn(
        new ArrayList<>(List.of(groupWithSubject)));
    when(studentGroupRepository.findBySubjects(any(Subject.class))).thenReturn(
        Collections.emptyList());
  }

  @Test
  void getByNonExistentId() {
    assertThrows(ResponseStatusException.class,
        () -> subjectService.getById("nonExistentId"));
  }

  @Test
  void getByExistentId() {
    Subject result = subjectService.getById(SUBJECT_ID);
    assertNotNull(result);
    assertEquals(SUBJECT_ID, result.getId());
    assertFalse(result.getTags().isEmpty());
    assertEquals(TAG_ID, result.getTags().getFirst().getId());
  }

  @Test
  void createSubject() {
    SubjectRequestDto requestDto =
        new SubjectRequestDto("Mathe", "blue", new ArrayList<>(List.of("tagId")));
    subjectService.create(requestDto);

    ArgumentCaptor<Subject> subjectCap = ArgumentCaptor.forClass(Subject.class);
    verify(subjectRepository, times(1)).save(subjectCap.capture());

    Subject subject = subjectCap.getValue();
    assertNotNull(subject);
    assertEquals("Mathe", subject.getName());

    assertEquals("blue", subject.getColor());

    assertEquals(1, subject.getTags().size());
    assertEquals("tagId", subject.getTags().getFirst().getId());
  }

  @Test
  void createSubjectEmptyName() {
    SubjectRequestDto requestDto =
        new SubjectRequestDto("", "blue", new ArrayList<>(List.of("tagId")));
    assertThrows(ResponseStatusException.class, () -> subjectService.create(requestDto));
  }

  @Test
  void updateSubject() {
    SubjectRequestDto requestDto =
        new SubjectRequestDto("Englisch", "red", new ArrayList<>(List.of("otherTagId")));
    subjectService.update(SUBJECT_ID, requestDto);

    ArgumentCaptor<Subject> subjectCap = ArgumentCaptor.forClass(Subject.class);
    verify(subjectRepository, times(1)).save(subjectCap.capture());

    Subject subject = subjectCap.getValue();
    assertNotNull(subject);
    assertEquals("Englisch", subject.getName());

    assertEquals("red", subject.getColor());

    assertEquals(1, subject.getTags().size());
    assertEquals("otherTagId", subject.getTags().getFirst().getId());
  }

  @Test
  void updateSubjectEmptyName() {
    SubjectRequestDto requestDto =
        new SubjectRequestDto("", "blue", new ArrayList<>(List.of("tagId")));
    assertThrows(ResponseStatusException.class, () -> subjectService.update("123", requestDto));
  }

  @Test
  void deleteExistentSubject() {
    assertDoesNotThrow(() -> subjectService.deleteSubjects(new String[] {SUBJECT_ID}));
    ArgumentCaptor<List<Subject>> subjectCap = ArgumentCaptor.forClass(getClassType());
    verify(subjectRepository, times(1)).deleteAll(subjectCap.capture());

    List<Subject> subject = subjectCap.getValue();
    assertEquals(1, subject.size());
    assertEquals(SUBJECT_ID, subject.getFirst().getId());
  }

  @Test
  void deleteNonExistentSubject() {
    assertThrows(ResponseStatusException.class,
        () -> subjectService.deleteSubjects(new String[] {"nonExistentId"}));
  }

  @Test
  void deleteSubjectsSomeExistent() {
    assertThrows(ResponseStatusException.class,
        () -> subjectService.deleteSubjects(new String[] {"nonExistentId", "123"}));
  }
}
