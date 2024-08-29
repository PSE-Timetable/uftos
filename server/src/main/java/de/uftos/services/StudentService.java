package de.uftos.services;

import de.uftos.dto.requestdtos.StudentRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Lesson;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /students endpoint.
 */
@Service
public class StudentService {
  private final StudentRepository repository;
  private final StudentGroupRepository studentGroupRepository;
  private final ServerRepository serverRepository;

  /**
   * Creates a student service.
   *
   * @param repository             the repository for accessing the student table.
   * @param studentGroupRepository the repository for accessing the student group table.
   * @param serverRepository       the repository for accessing the server table.
   */
  @Autowired
  public StudentService(StudentRepository repository,
                        StudentGroupRepository studentGroupRepository, ServerRepository serverRepository) {
    this.repository = repository;
    this.studentGroupRepository = studentGroupRepository;
    this.serverRepository = serverRepository;
  }

  /**
   * Gets a page of entries of the student table.
   *
   * @param pageable contains the parameters for the page.
   * @param search   the search filter.
   * @param groups   the student group filter.
   * @param tags     the tags filter.
   * @return the page of the entries fitting the parameters.
   */
  public Page<Student> get(Pageable pageable, Optional<String> search,
                           Optional<String[]> groups, Optional<String[]> tags) {
    Specification<Student> spec = new SpecificationBuilder<Student>()
        .search(search)
        .optionalAndJoinIn(groups, "groups", "id")
        .optionalAndJoinIn(tags, "tags", "id")
        .build();
    return this.repository.findAll(spec, pageable);
  }

  /**
   * Gets a student from their ID.
   *
   * @param id the ID of the student.
   * @return the student with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding student.
   */
  public Student getById(String id) {
    Optional<Student> student = this.repository.findById(id);

    return student.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Could not find a student with this id"));
  }

  /**
   * Gets the information about the lessons that the student attends.
   *
   * @param id the ID of the student.
   * @return a LessonResponseDto containing information about the lessons.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding student.
   */
  public LessonResponseDto getLessonsById(String id) {
    Student student = getById(id);
    List<StudentGroup> studentGroups = student.getGroups();
    List<Lesson> lessons = new ArrayList<>(
        studentGroups.stream().flatMap(group -> group.getLessons().stream()).toList());
    lessons.removeIf(lesson -> !lesson.getYear().equals(serverRepository.findAll().getFirst().getCurrentYear()));
    return LessonResponseDto.createResponseDtoFromLessons(lessons);
  }

  /**
   * Creates a new student in the database.
   *
   * @param rawStudent the student which is to be created.
   * @return the updated student which includes the ID that has been assigned.
   * @throws ResponseStatusException is thrown if the first or last name of the student is blank.
   */
  public Student create(StudentRequestDto rawStudent) {
    if (rawStudent.firstName().isBlank() || rawStudent.lastName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The first or last name of the student is blank.");
    }
    Student student = rawStudent.map();
    return this.repository.save(student);
  }

  /**
   * Updates the student with the given ID.
   *
   * @param id             the ID of the student which is to be updated.
   * @param studentRequest the updated student information.
   * @return the updated student.
   * @throws ResponseStatusException is thrown if the first or last name of the student is blank.
   */
  public Student update(String id, StudentRequestDto studentRequest) {
    if (studentRequest.firstName().isBlank() || studentRequest.lastName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The first or last name of the student is blank.");
    }
    Student student = studentRequest.map();
    student.setId(id);
    return this.repository.save(student);
  }

  /**
   * Deletes the student with the given ID.
   *
   * @param id the ID of the student which is to be deleted.
   * @throws ResponseStatusException is thrown if no student exists with the given ID.
   */
  public void delete(String id) {
    Optional<Student> student = this.repository.findById(id);
    if (student.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Could not find a student with this id");
    }
    List<StudentGroup> studentGroups = studentGroupRepository.findByStudents(student.get());
    for (StudentGroup group : studentGroups) {
      group.getStudents().removeIf(student1 -> student1.getId().equals(id));
    }
    studentGroupRepository.saveAll(studentGroups);

    this.repository.delete(student.get());
  }

  /**
   * Deletes the students with the given IDs.
   *
   * @param ids the IDs of the students which are to be deleted.
   * @throws ResponseStatusException is thrown if no students exist with the given IDs.
   */
  public void deleteStudents(String[] ids) {
    Specification<Student> studentSpecification = new SpecificationBuilder<Student>()
        .andIn(ids, "id")
        .build();
    List<Student> students = this.repository.findAll(studentSpecification);
    if (students.isEmpty() || students.size() != ids.length) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "There exist no students with the given id(s).");
    }
    Specification<StudentGroup> groupSpecification = new SpecificationBuilder<StudentGroup>()
        .optionalAndJoinIn(Optional.of(ids), "students", "id")
        .build();

    List<StudentGroup> studentGroups = this.studentGroupRepository.findAll(groupSpecification);
    for (StudentGroup group : studentGroups) {
      group.getStudents().removeIf(students::contains);
    }

    studentGroupRepository.saveAll(studentGroups);
    this.repository.deleteAllById(Arrays.stream(ids).toList());

  }
}
