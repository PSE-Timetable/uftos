package de.uftos.services;


import de.uftos.dto.requestdtos.GradeRequestDto;
import de.uftos.dto.responsedtos.GradeResponseDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.StudentGroup;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
   * @param sort contains the sort parameters.
   * @param name the name filter.
   * @param tags the tags filter.
   * @return the page of the entries fitting the parameters.
   */
  public List<GradeResponseDto> get(Sort sort, Optional<String> name,
                                    Optional<String[]> tags) {
    Specification<Grade> spec = new SpecificationBuilder<Grade>()
        .optionalOrLike(name, "name")
        .optionalAndJoinIn(tags, "tags", "id")
        .build();

    List<Grade> grades = this.repository.findAll(spec, sort);
    return grades.stream().map(this::mapResponseDto).toList();
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
   * @throws ResponseStatusException is thrown if the name of the grade is blank.
   */
  public GradeResponseDto create(GradeRequestDto grade) {
    if (grade.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The name of the grade is blank.");
    }
    return this.mapResponseDto(this.repository.save(grade.map()));
  }

  /**
   * Updates the grade with the given ID.
   *
   * @param id           the ID of the grade which is to be updated.
   * @param gradeRequest the updated grade information
   * @return the updated grade.
   * @throws ResponseStatusException is thrown if the name of the grade is blank.
   */
  public GradeResponseDto update(String id, GradeRequestDto gradeRequest) {
    if (gradeRequest.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The name of the grade is blank.");
    }
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
    return GradeResponseDto.createResponseDtoFromGrade(grade);
  }
}
