package de.uftos.controller;

import de.uftos.dto.requestdtos.StudentRequestDto;
import de.uftos.entities.Student;
import de.uftos.services.StudentService;
import java.util.Optional;
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

/**
 * The REST controller for the student entity.
 * This controller handles /students HTTP requests.
 */
@RestController
@RequestMapping("/students")
public class StudentController {
  private final StudentService studentService;

  /**
   * Creates the student controller.
   *
   * @param studentService the service for the student entity.
   */
  @Autowired
  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  /**
   * Maps the HTTP POST request, to create a new student in the database, to the
   * {@link StudentService#create(StudentRequestDto) create} function of the student service.
   *
   * @param student the student which is to be created.
   * @return the created student with the assigned ID.
   */
  @PostMapping()
  public Student createStudent(@RequestBody StudentRequestDto student) {
    return this.studentService.create(student);
  }

  /**
   * Maps the HTTP GET request for a set of students from the database, to the
   * {@link StudentService#get(Pageable, Optional, Optional, Optional)  get} function of
   * the student service.
   *
   * @param pageable  contains the parameters for the page.
   * @param firstName the first name filter.
   * @param lastName  the last name filter.
   * @param tags      the tags filter.
   * @return the page of students fitting the parameters.
   */
  @GetMapping()
  public Page<Student> getStudents(Pageable pageable, Optional<String> firstName,
                                   Optional<String> lastName, Optional<String[]> tags) {
    return this.studentService.get(pageable, firstName, lastName, tags);
  }

  /**
   * Maps the HTTP GET request for a student with the given ID to the
   * {@link StudentService#getById(String) getById} function of the student service.
   *
   * @param id the ID of the student.
   * @return the student with the given ID.
   */
  @GetMapping("/{id}")
  public Student getStudent(@PathVariable String id) {
    return this.studentService.getById(id);
  }

  /**
   * Maps the HTTP PUT request to update a student to the
   * {@link StudentService#update(String, StudentRequestDto) update} function of
   * the student service.
   *
   * @param id      the ID of the student which is to be updated.
   * @param student the updated information of the student.
   * @return the updated student.
   */
  @PutMapping("/{id}")
  public Student updateStudent(@PathVariable String id, @RequestBody StudentRequestDto student) {
    return this.studentService.update(id, student);
  }

  /**
   * Maps the HTTP DELETE request to the {@link StudentService#delete(String) delete} function of
   * the student service.
   *
   * @param id the ID of the student which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteStudent(@PathVariable String id) {
    this.studentService.delete(id);
  }
}
