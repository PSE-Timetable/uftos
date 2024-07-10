package de.uftos.services;

import de.uftos.builders.SpecificationBuilder;
import de.uftos.dto.GradeRequestDto;
import de.uftos.dto.GradeResponseDto;
import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.*;
import de.uftos.repositories.database.GradeRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.uftos.repositories.database.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /grades endpoint.
 */
@Service
public class GradeService {
  private final GradeRepository repository;
  private final ServerRepository serverRepository;

  /**
   * Creates a grade service.
   *
   * @param repository the repository for accessing the grade table.
   */
  @Autowired
  public GradeService(GradeRepository repository, ServerRepository serverRepository) {
    this.repository = repository;
    this.serverRepository = serverRepository;
  }

  /**
   * Gets a page of entries of the grade table.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @param tags     the tags filter.
   * @return the page of the entries fitting the parameters.
   */
  public Page<GradeResponseDto> get(Pageable pageable, Optional<String> name,
                                    Optional<String[]> tags) {
    Specification<Grade> spec = new SpecificationBuilder<Grade>()
        .optionalOrEquals(name, "name")
        .optionalAndJoinIn(tags, "tags", "id")
        .build();

    Page<Grade> grades = this.repository.findAll(spec, pageable);
    List<GradeResponseDto> response = grades.map(this::mapResponseDto).stream().toList();

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
  public LessonResponseDto getLessonsById(String id) {
    Grade grade = this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    Stream<StudentGroup> studentGroupStream =
        Stream.of(grade.getStudentGroups()).flatMap(Collection::stream);
    // A lesson cannot have multiple student groups attending it => no duplicates
    // getLessons returns null if there is no lesson
    List<Lesson> lessons = studentGroupStream.map(StudentGroup::getLessons).filter(Objects::nonNull)
        .flatMap(Collection::stream).collect(Collectors.toList());


    lessons.removeIf(lesson -> !lesson.getYear().equals(
        serverRepository.findAll().getFirst().getCurrentYear()));

    return LessonResponseDto.createResponseDtoFromLessons(lessons);
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
