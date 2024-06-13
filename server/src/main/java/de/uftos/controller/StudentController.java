package de.uftos.controller;

import de.uftos.entities.Student;
import de.uftos.services.StudentService;
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
@RequestMapping("/students")
public class StudentController {
  private final StudentService studentService;

  @Autowired
  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @PostMapping()
  public Student createStudent(@RequestBody Student student) {
    return this.studentService.create(student);
  }

  @GetMapping()
  public Page<Student> getStudents(Pageable pageable) {
    return this.studentService.get(pageable);
  }

  @GetMapping("/{id}")
  public Student getStudent(@PathVariable String id) {
    return this.studentService.getById(id);
  }

  @PutMapping("/{id}")
  public Student updateStudent(@PathVariable String id, @RequestBody Student student) {
    return this.studentService.update(id, student);
  }

  @DeleteMapping("/{id}")
  public void deleteStudent(@PathVariable String id) {
    this.studentService.delete(id);
  }
}
