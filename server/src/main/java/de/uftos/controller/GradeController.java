package de.uftos.controller;

import de.uftos.dto.GradeRequestDto;
import de.uftos.dto.GradeResponseDto;
import de.uftos.dto.LessonResponseDto;
import de.uftos.services.GradeService;
import java.util.List;
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
  public GradeResponseDto createGrade(@RequestBody GradeRequestDto grade) {
    return this.gradeService.create(grade);
  }

  @GetMapping()
  public Page<GradeResponseDto> getGrades(Pageable pageable) {
    return this.gradeService.get(pageable);
  }

  @GetMapping("/{id}")
  public GradeResponseDto getGrade(@PathVariable String id) {
    return this.gradeService.getById(id);
  }

  @GetMapping("/{id}/lessons")
  public List<LessonResponseDto> getLessons(@PathVariable String id) {
    return this.gradeService.getLessonsById(id);
  }

  @PutMapping("/{id}")
  public GradeResponseDto updateGrade(@PathVariable String id, @RequestBody GradeRequestDto grade) {
    return this.gradeService.update(id, grade);
  }

  @DeleteMapping("/{id}")
  public void deleteGrade(@PathVariable String id) {
    this.gradeService.delete(id);
  }
}
