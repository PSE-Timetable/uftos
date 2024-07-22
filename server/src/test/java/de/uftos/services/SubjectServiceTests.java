package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.repositories.database.SubjectRepository;
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
    subject.setTags(List.of(tag));

    when(subjectRepository.findAll()).thenReturn(List.of(subject));
    when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
  }

  @Test
  void lessonsById() {
    Subject result = subjectService.getById(SUBJECT_ID);
    assertNotNull(result);
    assertEquals(SUBJECT_ID, result.getId());
    assertFalse(result.getTags().isEmpty());
    assertEquals(TAG_ID, result.getTags().getFirst().getId());
  }
}
