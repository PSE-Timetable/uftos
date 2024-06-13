package de.uftos.controller;

import de.uftos.entities.Subject;
import de.uftos.services.SubjectService;
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
@RequestMapping("/subjects")
public class SubjectController {
  private final SubjectService subjectService;

  @Autowired
  public SubjectController(SubjectService subjectService) {
    this.subjectService = subjectService;
  }

  @PostMapping()
  public Subject createSubject(@RequestBody Subject subject) {
    return this.subjectService.create(subject);
  }

  @GetMapping()
  public Page<Subject> getSubjects(Pageable pageable) {
    return this.subjectService.get(pageable);
  }

  @GetMapping("/{id}")
  public Subject getSubject(@PathVariable String id) {
    return this.subjectService.getById(id);
  }

  @PutMapping("/{id}")
  public Subject updateSubject(@PathVariable String id, @RequestBody Subject subject) {
    return this.subjectService.update(id, subject);
  }

  @DeleteMapping("/{id}")
  public void deleteSubject(@PathVariable String id) {
    this.subjectService.delete(id);
  }
}
