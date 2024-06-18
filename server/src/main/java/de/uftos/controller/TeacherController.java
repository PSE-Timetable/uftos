package de.uftos.controller;

import de.uftos.entities.Teacher;
import de.uftos.services.TeacherService;
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
 * The Rest controller for the teacher entity.
 * This controller handles /teachers HTTP requests.
 */
@RestController
@RequestMapping("/teachers")
public class TeacherController {
  private final TeacherService teacherService;

  /**
   * Creates a teacher controller.
   *
   * @param teacherService the service for the teacher entity.
   */
  @Autowired
  public TeacherController(TeacherService teacherService) {
    this.teacherService = teacherService;
  }

  /**
   * Maps the HTTP POST request, to create a new teacher in the database, to the
   * {@link TeacherService#create(Teacher) create} function of the teacher service.
   *
   * @param teacher the teacher which is to be created.
   * @return the created teacher with the assigned ID.
   */
  @PostMapping()
  public Teacher createTeacher(@RequestBody Teacher teacher) {
    return this.teacherService.create(teacher);
  }

  /**
   * Maps the HTTP GET request for a set of teachers from the database to the
   * {@link TeacherService#get(Pageable) get} function of the teacher service.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of teachers fitting the parameters.
   */
  @GetMapping()
  public Page<Teacher> getTeachers(Pageable pageable) {
    return this.teacherService.get(pageable);
  }

  /**
   * Maps the HTTP GET request for a teacher with the given ID to the
   * {@link TeacherService#getById(long) getById} function of the teacher service.
   *
   * @param id the ID of the teacher.
   * @return the teacher with the given ID.
   */
  @GetMapping("/{id}")
  public Teacher getTeacher(@PathVariable long id) {
    return this.teacherService.getById(id);
  }

  /**
   * Maps the HTTP PUT request to update a teacher to the
   * {@link TeacherService#update(long, Teacher) update} function of the teacher service.
   *
   * @param id the ID of the teacher which is to be updated.
   * @param teacher the updated information of the teacher.
   * @return the updated teacher.
   */
  @PutMapping("/{id}")
  public Teacher updateTeacher(@PathVariable long id, @RequestBody Teacher teacher) {
    return this.teacherService.update(id, teacher);
  }

  /**
   * Maps the HTTP DELETE request to the {@link TeacherService#delete(long) delete} function of the
   * teacher service.
   *
   * @param id the ID of the teacher which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteTeacher(@PathVariable long id) {
    this.teacherService.delete(id);
  }
}
