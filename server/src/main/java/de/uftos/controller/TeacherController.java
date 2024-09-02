package de.uftos.controller;

import de.uftos.dto.requestdtos.TeacherRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Teacher;
import de.uftos.services.TeacherService;
import java.util.Optional;
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
 * The REST controller for the teacher entity.
 * This controller handles /teachers HTTP requests.
 */
@Validated
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
   * {@link TeacherService#create(TeacherRequestDto) create} function of the teacher service.
   *
   * @param teacher the teacher which is to be created.
   * @return the created teacher with the assigned ID.
   */
  @PostMapping()
  public Teacher createTeacher(@RequestBody TeacherRequestDto teacher) {
    return this.teacherService.create(teacher);
  }

  /**
   * Maps the HTTP GET request for a set of teachers from the database to the
   * {@link TeacherService#get(Pageable, Optional, Optional, Optional) get}
   * function of the teacher service.
   *
   * @param pageable contains the parameters for the page.
   * @param search   the text to search across first and last name as well as the acronym.
   * @param subjects the subjects filter.
   * @param tags     the tags filter.
   * @return the page of teachers fitting the parameters.
   */
  @GetMapping()
  public Page<Teacher> getTeachers(Pageable pageable, Optional<String> search,
                                   Optional<String[]> subjects, Optional<String[]> tags) {
    return this.teacherService.get(pageable, search, subjects, tags);
  }

  /**
   * Maps the HTTP GET request for a teacher with the given ID to the
   * {@link TeacherService#getById(String) getById} function of the teacher service.
   *
   * @param id the ID of the teacher.
   * @return the teacher with the given ID.
   */
  @GetMapping("/{id}")
  public Teacher getTeacher(@PathVariable String id) {
    return this.teacherService.getById(id);
  }

  /**
   * Maps the HTTP GET request, to get the lessons that the teacher teaches, to the
   * {@link TeacherService#getLessonsById(String) getLessonsById} function of the teacher service.
   *
   * @param id the ID of the teacher.
   * @return information about the lessons that the teacher teaches.
   */
  @GetMapping("/{id}/lessons")
  public LessonResponseDto getTeacherLessons(@PathVariable String id) {
    return this.teacherService.getLessonsById(id);
  }

  /**
   * Maps the HTTP PUT request to update a teacher to the
   * {@link TeacherService#update(String, TeacherRequestDto) update} function of the
   * teacher service.
   *
   * @param id      the ID of the teacher which is to be updated.
   * @param teacher the updated information of the teacher.
   * @return the updated teacher.
   */
  @PutMapping("/{id}")
  public Teacher updateTeacher(@PathVariable String id, @RequestBody TeacherRequestDto teacher) {
    return this.teacherService.update(id, teacher);
  }

  /**
   * Maps the HTTP DELETE request to the {@link TeacherService#deleteTeachers(String[]) delete} function of
   * the teacher service.
   *
   * @param ids the IDs of the teachers which are to be deleted.
   */
  @DeleteMapping()
  public void deleteTeachers(@RequestBody String[] ids) {
    this.teacherService.deleteTeachers(ids);
  }
}
