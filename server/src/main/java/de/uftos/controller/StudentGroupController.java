package de.uftos.controller;

import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.services.StudentGroupService;
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
@RequestMapping("/student-groups")
public class StudentGroupController {
  private final StudentGroupService studentGroupService;

  @Autowired
  public StudentGroupController(StudentGroupService studentGroupService) {
    this.studentGroupService = studentGroupService;
  }

  @PostMapping()
  public StudentGroup createStudentGroup(@RequestBody StudentGroup student) {
    return this.studentGroupService.create(student);
  }

  @GetMapping()
  public Page<StudentGroup> getStudentGroups(Pageable pageable) {
    return this.studentGroupService.get(pageable);
  }

  @GetMapping("/{id}")
  public StudentGroup getStudentGroup(@PathVariable String id) {
    return this.studentGroupService.getById(id);
  }

  @PostMapping("/{id}/students")
  public void addStudents(@PathVariable String id, @RequestBody List<Student> students) {
    this.studentGroupService.addStudents(id, students);
  }

  @DeleteMapping("/{id}/students")
  public void removeStudents(@PathVariable String id, @RequestBody List<Student> students) {
    this.studentGroupService.removeStudents(id, students);
  }


  @PutMapping("/{id}")
  public StudentGroup updateStudentGroup(@PathVariable String id,
                                         @RequestBody StudentGroup student) {
    return this.studentGroupService.update(id, student);
  }

  @DeleteMapping("/{id}")
  public void deleteStudentGroup(@PathVariable String id) {
    this.studentGroupService.delete(id);
  }
}
