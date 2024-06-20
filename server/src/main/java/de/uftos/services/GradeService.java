package de.uftos.services;

import de.uftos.dto.GradeRequestDto;
import de.uftos.dto.GradeResponseDto;
import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.Grade;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.repositories.database.GradeRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service for accessing the grade database entity.
 */
@Service
public class GradeService {
  private final GradeRepository repository;

  /**
   * Creates a grade service.
   *
   * @param repository the repository for accessing the grade entity.
   */
  @Autowired
  public GradeService(GradeRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the grade entity.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of the entries fitting the parameters.
   */
  public Page<GradeResponseDto> get(Pageable pageable) {
    Page<Grade> grades = this.repository.findAll(pageable);
    List<GradeResponseDto> response =
        grades.map(this::mapResponseDto).stream().toList();

    return new PageImpl<>(response, pageable, response.size());
  }

  /**
   * Gets a grade from their ID.
   *
   * @param id the ID of the grade.
   * @return the grade with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding grade.
   */
  public GradeResponseDto getById(String id) {
    Grade grade = this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    return this.mapResponseDto(grade);
  }

  /**
   * Gets the information about the lessons that are taught in the grade.
   *
   * @param id the ID of the grade.
   * @return a list of objects each containing information about a lesson.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding grade.
   */
  public List<LessonResponseDto> getLessonsById(String id) {
    Grade grade = this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    // TODO
    return null;
  }

  /**
   * Creates a new grade in the database.
   *
   * @param grade the information about the grade which is to be created.
   * @return the created grade which includes the ID that has been assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the grade parameter
   *                                 is already present in the database.
   */
  public GradeResponseDto create(GradeRequestDto grade) {
    return this.mapResponseDto(this.repository.save(grade.map()));
  }

  /**
   * Updates the grade with the given ID.
   *
   * @param id           the ID of the grade which is to be updated.
   * @param gradeRequest the updated grade information
   * @return the updated grade.
   */
  public GradeResponseDto update(String id, GradeRequestDto gradeRequest) {
    Grade grade = gradeRequest.map();
    grade.setId(id);

    return this.mapResponseDto(this.repository.save(grade));
  }

  /**
   * Deletes the grade with the given ID.
   *
   * @param id the ID of the grade which is to be deleted.
   * @throws ResponseStatusException is thrown if no grade exists with the given ID.
   */
  public void delete(String id) {
    var grade = this.repository.findById(id);
    if (grade.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(grade.get());
  }

  private GradeResponseDto mapResponseDto(Grade grade) {
    Set<String> studentIds = new HashSet<>();

    for (StudentGroup group : grade.getStudentGroups()) {
      studentIds.addAll(group.getStudents().stream().map(Student::getId).toList());
    }

    return new GradeResponseDto(grade.getId(), grade.getName(),
        grade.getStudentGroups().stream().map(StudentGroup::getId).toList(),
        studentIds.stream().toList(),
        grade.getTags());
  }
}
