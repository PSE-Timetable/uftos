package de.uftos.controller;

import de.uftos.dto.StudentGroupRequestDto;
import de.uftos.entities.StudentGroup;
import de.uftos.services.StudentGroupService;
import java.util.List;
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
 * The REST controller for the student group entity.
 * This controller handles /student-groups HTTP requests.
 */
@RestController
@RequestMapping("/student-groups")
public class StudentGroupController {
  private final StudentGroupService studentGroupService;

  /**
   * Creates the student group controller.
   *
   * @param studentGroupService the service for the student group entity.
   */
  @Autowired
  public StudentGroupController(StudentGroupService studentGroupService) {
    this.studentGroupService = studentGroupService;
  }

  /**
   * Maps the HTTP POST request, to create a new student group in the database, to the
   * {@link StudentGroupService#create(StudentGroupRequestDto) create}
   * function of the student group service.
   *
   * @param studentGroup the student group which is to be created.
   * @return the created student group with the assigned ID.
   */
  @PostMapping()
  public StudentGroup createStudentGroup(@RequestBody StudentGroupRequestDto studentGroup) {
    return this.studentGroupService.create(studentGroup);
  }

  /**
   * Maps the HTTP GET request for a set of student groups from the database, to the
   * {@link StudentGroupService#get(Pageable, Optional)  get} function of the student group service.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @return the page of student groups fitting the parameters.
   */
  @GetMapping()
  public Page<StudentGroup> getStudentGroups(Pageable pageable, Optional<String> name) {
    return this.studentGroupService.get(pageable, name);
  }

  /**
   * Maps the HTTP GET request for a student group with the given ID to the
   * {@link StudentGroupService#getById(String) getById} function of the student group service.
   *
   * @param id the ID of the student group.
   * @return The student group with the given ID.
   */
  @GetMapping("/{id}")
  public StudentGroup getStudentGroup(@PathVariable String id) {
    return this.studentGroupService.getById(id);
  }

  /**
   * Maps the HTTP POST request, to add students to a student group, to the
   * {@link StudentGroupService#addStudents(String, List) addStudents} function of the
   * student group service.
   *
   * @param id         the ID of the student group.
   * @param studentIds the IDs of students which are to be added.
   */
  @PostMapping("/{id}/students")
  public StudentGroup addStudents(@PathVariable String id, @RequestBody List<String> studentIds) {
    return this.studentGroupService.addStudents(id, studentIds);
  }

  /**
   * Maps the HTTP DELETE request, to remove students from a student group, to the
   * {@link StudentGroupService#removeStudents(String, List) removeStudent} function of the
   * student group service.
   *
   * @param id         the ID of the student group.
   * @param studentIds the students which are to be removed.
   */
  @DeleteMapping("/{id}/students")
  public void removeStudents(@PathVariable String id, @RequestBody List<String> studentIds) {
    this.studentGroupService.removeStudents(id, studentIds);
  }

  /**
   * Maps the HTTP PUT request to update a student group to the
   * {@link StudentGroupService#update(String, StudentGroupRequestDto) update} function of the
   * student group service.
   *
   * @param id           the ID of the student group which is to be updated.
   * @param studentGroup the updated information of the student group.
   * @return the updated student group.
   */
  @PutMapping("/{id}")
  public StudentGroup updateStudentGroup(@PathVariable String id,
                                         @RequestBody StudentGroupRequestDto studentGroup) {
    return this.studentGroupService.update(id, studentGroup);
  }


  /**
   * Maps the HTTP DELETE request to the {@link StudentGroupService#delete(String) delete} function
   * of the student group service.
   *
   * @param id the ID of the student group which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteStudentGroup(@PathVariable String id) {
    this.studentGroupService.delete(id);
  }
}
