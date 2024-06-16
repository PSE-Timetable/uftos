package de.uftos.controller;

import de.uftos.entities.Lesson;
import de.uftos.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lessons")
public class LessonController {
  private final LessonService lessonsService;

  @Autowired
  public LessonController(LessonService lessonsService) {
    this.lessonsService = lessonsService;
  }

  @PostMapping()
  public Lesson createLesson(@RequestBody Lesson lessons) {
    return this.lessonsService.create(lessons);
  }

  @GetMapping()
  public Page<Lesson> getLessons(Pageable pageable) {
    return this.lessonsService.get(pageable);
  }

  @GetMapping("/{id}")
  public Lesson getLesson(@PathVariable long id) {
    return this.lessonsService.getById(id);
  }

  @PutMapping("/{id}")
  public Lesson updateLesson(@PathVariable long id, @RequestBody Lesson lessons) {
    return this.lessonsService.update(id, lessons);
  }

  @DeleteMapping("/{id}")
  public void deleteLesson(@PathVariable long id) {
    this.lessonsService.delete(id);
  }
}
