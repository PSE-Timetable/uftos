package de.uftos.services;


import de.uftos.dto.SuccessResponse;
import de.uftos.dto.requestdtos.GradeRequestDto;
import de.uftos.dto.responsedtos.GradeResponseDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.LessonsCount;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.utils.ConstraintInstanceDeleter;
import de.uftos.utils.SpecificationBuilder;
import java.util.ArrayList;
import java.util.Arrays;
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
  private final ConstraintSignatureRepository constraintSignatureRepository;
  private final ConstraintInstanceRepository constraintInstanceRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final CurriculumRepository curriculumRepository;
  private final SubjectRepository subjectRepository;

  /**
   * Creates a grade service.
   *
   * @param repository                    the repository for accessing the grade table.
   * @param serverRepository              the repository for accessing the server table.
   * @param studentGroupRepository        the repository for accessing the student group table.
   * @param curriculumRepository          the repository for accessing the curriculum table.
   * @param subjectRepository             the repository for accessing the subject table.
   * @param constraintSignatureRepository the repository for accessing the constraint signature table.
   * @param constraintInstanceRepository  the repository for accessing the constraint instance table.
   */
  @Autowired
  public GradeService(GradeRepository repository, ServerRepository serverRepository,
                      StudentGroupRepository studentGroupRepository,
                      CurriculumRepository curriculumRepository,
                      SubjectRepository subjectRepository,
                      ConstraintSignatureRepository constraintSignatureRepository,
                      ConstraintInstanceRepository constraintInstanceRepository) {
    this.repository = repository;
    this.serverRepository = serverRepository;
    this.constraintSignatureRepository = constraintSignatureRepository;
    this.constraintInstanceRepository = constraintInstanceRepository;
    this.studentGroupRepository = studentGroupRepository;
    this.curriculumRepository = curriculumRepository;
    this.subjectRepository = subjectRepository;
  }

  /**
   * Gets a page of entries of the grade table.
   *
   * @param sort   contains the sort parameters.
   * @param search the search filter.
   * @param tags   the tags filter.
   * @return the page of the entries fitting the parameters.
   */
  public List<GradeResponseDto> get(Sort sort, Optional<String> search,
                                    Optional<String[]> tags) {
    Specification<Grade> spec = new SpecificationBuilder<Grade>()
        .search(search)
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
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Could not find a grade with this id"));

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
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Could not find a grade with this id"));

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
    Grade gradeEntity = this.repository.save(grade.map());
    List<Subject> subjects = subjectRepository.findAll();
    List<LessonsCount> lessonsCounts = new ArrayList<>();
    for (Subject subject : subjects) {
      lessonsCounts.add(new LessonsCount(subject.getId(), 0));
    }

    Curriculum curriculum = new Curriculum();
    curriculum.setGrade(gradeEntity);
    curriculum.setName(gradeEntity.getName());
    curriculum.setLessonsCounts(lessonsCounts);
    curriculumRepository.save(curriculum);

    return this.mapResponseDto(gradeEntity);
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
   * @param ids the IDs of the grades which are to be deleted.
   * @throws ResponseStatusException is thrown if no grade exists with the given ID.
   */
  public SuccessResponse deleteGrades(String[] ids) {
    List<String> gradesIds = Arrays.asList(ids);
    List<Grade> grades = this.repository.findAllById(gradesIds);
    if (grades.size() != gradesIds.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Could not find a grade with this id");
    }

    List<StudentGroup> studentGroups =
        this.studentGroupRepository.findAllByGrades(gradesIds);
    if (!studentGroups.isEmpty()) {
      return new SuccessResponse(false, "Diese Stufe ist noch mit einer Schülergruppe verbunden");
    }

    new ConstraintInstanceDeleter(constraintSignatureRepository, constraintInstanceRepository)
        .removeAllInstancesWithArgumentValue(ids);

    this.repository.deleteAll(grades);
    return new SuccessResponse(true, "Stufe erfolgreich gelöscht");
  }

  private GradeResponseDto mapResponseDto(Grade grade) {
    return GradeResponseDto.createResponseDtoFromGrade(grade);
  }
}
