package de.uftos.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uftos.dto.requestdtos.TagRequestDto;
import de.uftos.entities.Break;
import de.uftos.entities.Server;
import de.uftos.entities.Tag;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.TagRepository;
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

  @InjectMocks
  private TagService tagService;

  @BeforeEach
  void setUp() {
    Tag tag = new Tag("", "Sehbehinderung");
    tag.setId("123");

    Server server =
        new Server(new TimetableMetadata(45, 8, "7:45", new Break[] {}), "2024");
    when(serverRepository.findAll()).thenReturn(List.of(server));
    when(tagRepository.findById("123")).thenReturn(Optional.of(tag));
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
    assertEquals("Sehbehinderung", result.getName());
  }

  @Test
  void createTag() {
    TagRequestDto requestDto =
        new TagRequestDto("Rollstuhl");
    tagService.create(requestDto);

    ArgumentCaptor<Tag> tagCap = ArgumentCaptor.forClass(Tag.class);
    verify(tagRepository, times(1)).save(tagCap.capture());

    Tag tag = tagCap.getValue();
    assertNotNull(tag);
    assertEquals("Rollstuhl", tag.getName());
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
    assertDoesNotThrow(() -> tagService.delete("123"));
    ArgumentCaptor<Tag> tagCap = ArgumentCaptor.forClass(Tag.class);
    verify(tagRepository, times(1)).delete(tagCap.capture());

    Tag tag = tagCap.getValue();
    assertEquals("123", tag.getId());
  }

  @Test
  void deleteNonExistentTimeslot() {
    assertThrows(ResponseStatusException.class, () -> tagService.delete("nonExistentId"));
  }

}
