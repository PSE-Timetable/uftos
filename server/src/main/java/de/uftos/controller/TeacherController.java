package de.uftos.controller;

import de.uftos.dto.LessonResponseDto;
import de.uftos.dto.TeacherRequestDto;
import de.uftos.entities.Teacher;
import de.uftos.services.TeacherService;
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
@RequestMapping("/teachers")
public class TeacherController {
  private final TeacherService teacherService;

  @Autowired
  public TeacherController(TeacherService teacherService) {
    this.teacherService = teacherService;
  }

  @PostMapping()
  public Teacher createTeacher(@RequestBody TeacherRequestDto teacher) {
    return this.teacherService.create(teacher);
  }

  @GetMapping()
  public Page<Teacher> getTeachers(Pageable pageable) {
    return this.teacherService.get(pageable);
  }

  @GetMapping("/{id}")
  public Teacher getTeacher(@PathVariable String id) {
    return this.teacherService.getById(id);
  }

  @GetMapping("/{id}/lessons")
  public List<LessonResponseDto> getLessons(@PathVariable String id) {
    return this.teacherService.getLessonsById(id);
  }

  @PutMapping("/{id}")
  public Teacher updateTeacher(@PathVariable String id, @RequestBody TeacherRequestDto teacher) {
    return this.teacherService.update(id, teacher);
  }

  @DeleteMapping("/{id}")
  public void deleteTeacher(@PathVariable String id) {
    this.teacherService.delete(id);
  }
}
