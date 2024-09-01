package de.uftos.controller;

import de.uftos.dto.requestdtos.LessonRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Lesson;
import de.uftos.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for the lesson entity.
 * This controller handles /lessons HTTP requests.
 */
@Validated
@RestController
@RequestMapping("/lessons")
public class LessonController {
  private final LessonService lessonsService;

  /**
   * Creates a lesson controller.
   *
   * @param lessonsService the service for the lesson entity.
   */
  @Autowired
  public LessonController(LessonService lessonsService) {
    this.lessonsService = lessonsService;
  }

  /**
   * Maps the HTTP POST request, to create a new lesson in the database, to the
   * {@link LessonService#create(LessonRequestDto) create} function of the lesson service.
   *
   * @param lesson the lessons which is to be created.
   * @return the created lesson with the assigned ID.
   */
  @PostMapping()
  public Lesson createLesson(@RequestBody LessonRequestDto lesson) {
    return this.lessonsService.create(lesson);
  }

  /**
   * Maps the HTTP GET request for a set of lessons from the database to the
   * {@link LessonService#get(Pageable) get} function of the lesson service.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of lessons fitting the parameters.
   */
  @GetMapping()
  public Page<LessonResponseDto> getLessons(Pageable pageable) {
    return this.lessonsService.get(pageable);
  }

  /**
   * Maps the HTTP GET request for a lesson with the given ID to the
   * {@link LessonService#getById(String) getById} function of the lesson service.
   *
   * @param id the ID of the lesson.
   * @return the lesson with the given ID.
   */
  @GetMapping("/{id}")
  public Lesson getLesson(@PathVariable String id) {
    return this.lessonsService.getById(id);
  }

  /**
   * Maps the HTTP PUT request to update a lesson to the
   * {@link LessonService#update(String, LessonRequestDto) update} function of the lesson service.
   *
   * @param id     the ID of the lesson which is to be updated.
   * @param lesson the updated information of the lesson.
   * @return the updated lesson.
   */
  @PutMapping("/{id}")
  public Lesson updateLesson(@PathVariable String id, @RequestBody LessonRequestDto lesson) {
    return this.lessonsService.update(id, lesson);
  }

  /**
   * Maps the HTTP DELETE request to the {@link LessonService#delete(String) delete} function of the
   * lesson service.
   *
   * @param id the ID of the lesson which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteLesson(@PathVariable String id) {
    this.lessonsService.delete(id);
  }
}
