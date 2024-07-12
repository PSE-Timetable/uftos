package de.uftos.services;

import de.uftos.dto.LessonRequestDto;
import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.Lesson;
import de.uftos.repositories.database.LessonRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /lessons endpoint.
 */
@Service
public class LessonService {
  private final LessonRepository repository;

  /**
   * Creates a lesson service.
   *
   * @param repository the repository for accessing the lesson table.
   */
  @Autowired
  public LessonService(LessonRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the lesson table.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of entries fitting the parameters.
   */
  public Page<LessonResponseDto> get(Pageable pageable) {
    List<Lesson> lessons = this.repository.findAll(pageable).stream().toList();
    LessonResponseDto dto = LessonResponseDto.createResponseDtoFromLessons(lessons);

    //TODO filter out the lessons from another year?
    //TODO use page or just send the response Dto itself?

    return new PageImpl<>(List.of(dto), PageRequest.of(0, 1), 1);
  }

  /**
   * Gets a lesson from their ID.
   *
   * @param id the ID of the lesson.
   * @return the lesson with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding lesson.
   */
  public Lesson getById(String id) {
    var lesson = this.repository.findById(id);

    return lesson.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new lesson in the database.
   *
   * @param lessonRequestDto the information about the lesson which is to be created.
   * @return the created lesson which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the lesson parameter is
   *                                 already present in the database.
   */
  public Lesson create(LessonRequestDto lessonRequestDto) {
    Lesson lesson = lessonRequestDto.map();
    if (this.repository.findById(lesson.getId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return this.repository.save(lesson);
  }

  /**
   * Updates the lesson with the given ID.
   *
   * @param id               the ID of the lesson which is to be updated.
   * @param lessonRequestDto the updated lesson information.
   * @return the updated lesson.
   */
  public Lesson update(String id, LessonRequestDto lessonRequestDto) {
    Lesson lesson = lessonRequestDto.map();
    lesson.setId(id);

    return this.repository.save(lesson);
  }

  /**
   * Deletes the lesson with the given ID.
   *
   * @param id the ID of the lesson which is to be deleted.
   * @throws ResponseStatusException is thrown if no lesson exists with the given ID.
   */
  public void delete(String id) {
    var lesson = this.repository.findById(id);
    if (lesson.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(lesson.get());
  }
}
