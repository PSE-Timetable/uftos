package de.uftos.services;

import de.uftos.dto.requestdtos.SubjectRequestDto;
import de.uftos.entities.Curriculum;
import de.uftos.entities.LessonsCount;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /subjects endpoint.
 */
@Service
public class SubjectService {
  private final SubjectRepository repository;
  private final CurriculumRepository curriculumRepository;
  private final TeacherRepository teacherRepository;
  private final StudentGroupRepository studentGroupRepository;

  /**
   * Creates a subject service.
   *
   * @param repository             The repository for accessing the subject table.
   * @param curriculumRepository   The repository for accessing the curriculum table.
   * @param teacherRepository      The repository for accessing the teacher table.
   * @param studentGroupRepository The repository for accessing the student group table.
   */
  @Autowired
  public SubjectService(SubjectRepository repository, CurriculumRepository curriculumRepository,
                        TeacherRepository teacherRepository,
                        StudentGroupRepository studentGroupRepository) {
    this.repository = repository;
    this.curriculumRepository = curriculumRepository;
    this.teacherRepository = teacherRepository;
    this.studentGroupRepository = studentGroupRepository;
  }

  /**
   * Gets a page of entries of the subject table.
   *
   * @param sort   contains the sort parameters.
   * @param search the search filter.
   * @return the page of the entries fitting the parameters.
   */
  public List<Subject> get(Sort sort, Optional<String> search) {
    Specification<Subject> specification = new SpecificationBuilder<Subject>()
        .search(search)
        .build();

    return this.repository.findAll(specification, sort);
  }

  /**
   * Gets a subject from their ID.
   *
   * @param id the ID of the subject.
   * @return The subject with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding subject.
   */
  public Subject getById(String id) {
    Optional<Subject> subject = this.repository.findById(id);

    return subject.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Could not find a subject with this id"));
  }

  /**
   * Creates a new subject in the database.
   *
   * @param subject the subject which to be created.
   * @return The updated subject which includes the ID that has been assigned.
   * @throws ResponseStatusException is thrown if the name of the subject is blank.
   */
  public Subject create(SubjectRequestDto subject) {
    if (subject.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The name of the subject can not be empty!");
    }

    Subject subjectEntity = this.repository.save(subject.map());

    List<Curriculum> curricula = curriculumRepository.findAll();

    for (Curriculum curriculum : curricula) {
      curriculum.getLessonsCounts().add(new LessonsCount(subjectEntity.getId(), 0));
    }
    curriculumRepository.saveAll(curricula);

    return subjectEntity;
  }

  /**
   * Updates the subject with the given ID.
   *
   * @param id             the ID of the subject which is to be updated.
   * @param subjectRequest the updated subject information.
   * @return the updated subject.
   * @throws ResponseStatusException is thrown if the name of the subject is blank.
   */
  public Subject update(String id, SubjectRequestDto subjectRequest) {
    if (subjectRequest.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The name of the subject is blank.");
    }
    Subject subject = subjectRequest.map();
    subject.setId(id);

    return this.repository.save(subject);
  }

  /**
   * Deletes the subject with the given ID.
   *
   * @param id the ID of the subject which is to be deleted.
   * @throws ResponseStatusException is thrown if no subjects exist with the given IDs.
   */
  public void delete(String id) {
    Optional<Subject> subjectOptional = this.repository.findById(id);
    if (subjectOptional.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The name of the subject is blank.");
    }

    Subject subject = subjectOptional.get();

    List<Curriculum> curriculums = curriculumRepository.findAll();

    for (Curriculum curriculum : curriculums) {
      curriculum.getLessonsCounts()
          .removeIf((lessonsCount) -> lessonsCount.getSubject().equals(subject));
    }

    List<Teacher> teachers = teacherRepository.findBySubjects(subject);
    for (Teacher teacher : teachers) {
      teacher.getSubjects().removeIf(subject1 -> subject1.getId().equals(id));
    }
    teacherRepository.saveAll(teachers);

    List<StudentGroup> studentGroups = studentGroupRepository.findBySubjects(subject);
    for (StudentGroup studentGroup : studentGroups) {
      studentGroup.getSubjects().removeIf(subject1 -> subject1.getId().equals(id));
    }

    studentGroupRepository.saveAll(studentGroups);
    curriculumRepository.saveAll(curriculums);

    this.repository.delete(subject);
  }

  /**
   * Deletes the subjects with the given IDs.
   *
   * @param ids the IDs of the subjects which are to be deleted.
   * @throws ResponseStatusException is thrown if no subject exists with the given ID.
   */
  public void deleteSubjects(String[] ids) {
    Specification<Subject> subjectSpecification = new SpecificationBuilder<Subject>()
        .andIn(ids, "id")
        .build();
    List<Subject> subjects = this.repository.findAll(subjectSpecification);

    if (subjects.isEmpty() || subjects.size() != ids.length) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "There exist no subjects with the given id(s).");
    }

    List<String> subjectIds = Arrays.stream(ids).toList();

    Specification<Curriculum> curriculumSpecification = new SpecificationBuilder<Curriculum>()
        .andDoubleJoinIn(ids, "lessonsCounts", "subject", "id")
        .build();
    List<Curriculum> curricula = curriculumRepository.findAll(curriculumSpecification);

    for (Curriculum curriculum : curricula) {
      curriculum.getLessonsCounts()
          .removeIf((lessonsCount) -> subjectIds.contains(lessonsCount.getSubject().getId()));
    }
    curriculumRepository.saveAll(curricula);

    Specification<Teacher> teacherSpecification = new SpecificationBuilder<Teacher>()
        .optionalAndJoinIn(Optional.of(ids), "subjects", "id")
        .build();
    List<Teacher> teachers = teacherRepository.findAll(teacherSpecification);
    for (Teacher teacher : teachers) {
      teacher.getSubjects().removeIf(subject1 -> subjectIds.contains(subject1.getId()));
    }

    Specification<StudentGroup> studentGroupSpecification = new SpecificationBuilder<StudentGroup>()
        .optionalAndJoinIn(Optional.of(ids), "subjects", "id")
        .build();
    List<StudentGroup> studentGroups = studentGroupRepository.findAll(studentGroupSpecification);
    for (StudentGroup studentGroup : studentGroups) {
      studentGroup.getSubjects().removeIf(subject1 -> subjectIds.contains(subject1.getId()));
    }
    studentGroupRepository.saveAll(studentGroups);

    this.repository.deleteAll(subjects);
  }
}
