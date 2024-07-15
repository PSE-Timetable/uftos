package de.uftos.controller;

import de.uftos.dto.GradeRequestDto;
import de.uftos.dto.GradeResponseDto;
import de.uftos.dto.LessonResponseDto;
import de.uftos.services.GradeService;
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
 * The REST controller for the grade entity.
 * This controller handles /grades HTTP requests.
 */
@RestController
@RequestMapping("/grades")
public class GradeController {
  private final GradeService gradeService;

  /**
   * Creates the grade controller.
   *
   * @param gradeService the service for the grade entity.
   */
  @Autowired
  public GradeController(GradeService gradeService) {
    this.gradeService = gradeService;
  }

  /**
   * Maps the HTTP POST request, to create a new grade in the database, to the
   * {@link GradeService#create(GradeRequestDto) create} function of the grade service.
   *
   * @param grade the grade which is to be created.
   * @return the created grade with the assigned ID.
   */
  @PostMapping()
  public GradeResponseDto createGrade(@RequestBody GradeRequestDto grade) {
    return this.gradeService.create(grade);
  }

  /**
   * Maps the HTTP GET request for a set of grades from the database, to the
   * {@link GradeService#get(Pageable, Optional, Optional) get} function of the grade service.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @param tags     the tags filter.
   * @return the page of grades fitting the parameters.
   */
  @GetMapping()
  public Page<GradeResponseDto> getGrades(Pageable pageable, Optional<String> name, Optional<String[]> tags) {
    return this.gradeService.get(pageable, name, tags);
  }

  /**
   * Maps the HTTP GET request for a grade with the given ID to the
   * {@link GradeService#getById(String) getById} function of the grade service.
   *
   * @param id the ID of the grade.
   * @return the grade with the given ID.
   */
  @GetMapping("/{id}")
  public GradeResponseDto getGrade(@PathVariable String id) {
    return this.gradeService.getById(id);
  }

  /**
   * Maps the HTTP GET request, to get the lessons that are taught in the grade, to the
   * {@link GradeService#getLessonsById(String) getLessonsById} function of the grade service.
   *
   * @param id the ID of the grade.
   * @return information about the lessons that are taught in the grade.
   */
  @GetMapping("/{id}/lessons")
  public LessonResponseDto getGradeLessons(@PathVariable String id) {
    return this.gradeService.getLessonsById(id);
  }

  /**
   * Maps the HTTP PUT request to update a grade to the
   * {@link GradeService#update(String, GradeRequestDto) update} function of the grade service.
   *
   * @param id    the ID of the grade which is to be updated.
   * @param grade the updated information of the grade.
   * @return the updated grade.
   */
  @PutMapping("/{id}")
  public GradeResponseDto updateGrade(@PathVariable String id, @RequestBody GradeRequestDto grade) {
    return this.gradeService.update(id, grade);
  }

  /**
   * Maps the HTTP DELETE request to the {@link GradeService#delete(String) delete} function of the
   * grade service.
   *
   * @param id the ID of the grade which is to be deleted.
   */
  @DeleteMapping("/{id}")
  public void deleteGrade(@PathVariable String id) {
    this.gradeService.delete(id);
  }
}
