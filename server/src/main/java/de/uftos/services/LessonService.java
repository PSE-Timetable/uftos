package de.uftos.services;

import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.Lesson;
import de.uftos.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LessonService {
  private final LessonRepository repository;

  @Autowired
  public LessonService(LessonRepository repository) {
    this.repository = repository;
  }

  public Page<LessonResponseDto> get(Pageable pageable) {
    // TODO
    return null;
  }

  public Lesson getById(String id) {
    var lesson = this.repository.findById(id);

    return lesson.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Lesson create(Lesson lesson) {
    if (this.repository.findById(lesson.getId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return this.repository.save(lesson);
  }

  public Lesson update(String id, Lesson lesson) {
    lesson.setId(id);

    return this.repository.save(lesson);
  }

  public void delete(String id) {
    var lesson = this.repository.findById(id);
    if (lesson.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(lesson.get());
  }
}
