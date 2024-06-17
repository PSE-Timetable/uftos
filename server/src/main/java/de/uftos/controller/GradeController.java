package de.uftos.controller;

import de.uftos.entities.Grade;
import de.uftos.services.GradeService;
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
@RequestMapping("/grades")
public class GradeController {
  private final GradeService gradeService;

  @Autowired
  public GradeController(GradeService gradeService) {
    this.gradeService = gradeService;
  }

  @PostMapping()
  public Grade createGrade(@RequestBody Grade grade) {
    return this.gradeService.create(grade);
  }

  @GetMapping()
  public Page<Grade> getGrades(Pageable pageable) {
    return this.gradeService.get(pageable);
  }

  @GetMapping("/{id}")
  public Grade getGrade(@PathVariable long id) {
    return this.gradeService.getById(id);
  }

  @PutMapping("/{id}")
  public Grade updateGrade(@PathVariable long id, @RequestBody Grade grade) {
    return this.gradeService.update(id, grade);
  }

  @DeleteMapping("/{id}")
  public void deleteGrade(@PathVariable long id) {
    this.gradeService.delete(id);
  }
}
