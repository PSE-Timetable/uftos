package de.uftos.services;

import de.uftos.dto.requestdtos.StudentGroupRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Lesson;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /student-groups endpoint.
 */
@Service
public class StudentGroupService {
  private final StudentGroupRepository repository;
  private final ServerRepository serverRepository;
  private final StudentRepository studentRepository;

  /**
   * Creates a student group service.
   *
   * @param repository the repository for accessing the student group table.
   */
  @Autowired
  public StudentGroupService(StudentGroupRepository repository, ServerRepository serverRepository,
                             StudentRepository studentRepository) {
    this.repository = repository;
    this.serverRepository = serverRepository;
    this.studentRepository = studentRepository;
  }

  /**
   * Gets a page of entries of the student group table.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @param tags     the tags filter.
   * @return the page of the entries fitting the parameters.
   */
  public Page<StudentGroup> get(Pageable pageable, Optional<String> name, Optional<String[]> tags) {
    Specification<StudentGroup> spec = new SpecificationBuilder<StudentGroup>()
        .optionalOrEquals(name, "name")
        .optionalAndJoinIn(tags, "tags", "id")
        .build();
    return this.repository.findAll(spec, pageable);
  }


  /**
   * Gets a student group from their ID.
   *
   * @param id the ID of the student group.
   * @return the student group with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding student group.
   */
  public StudentGroup getById(String id) {
    Optional<StudentGroup> studentGroup = this.repository.findById(id);
    return studentGroup.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Gets the information about the lessons that the student group attends.
   *
   * @param id the ID of the student group.
   * @return a LessonResponseDto containing information about the lessons.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding student group.
   */
  public LessonResponseDto getLessonsById(String id) {
    StudentGroup studentGroup = getById(id);
    List<Lesson> lessons = new ArrayList<>(studentGroup.getLessons());
    lessons.removeIf(lesson -> !lesson.getYear().equals(
        serverRepository.findAll().getFirst().getCurrentYear()));
    return LessonResponseDto.createResponseDtoFromLessons(lessons);
  }

  /**
   * Creates a new student group in the database.
   *
   * @param group the student group which is to be created.
   * @return the updated student group including the ID which has been assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the student group parameter is
   *                                 already present in the database.
   */
  public StudentGroup create(StudentGroupRequestDto group) {
    return this.repository.save(group.map());
  }

  /**
   * Updates the student group with the given ID.
   *
   * @param id           the ID of the student group which is to be updated.
   * @param groupRequest the updated student information.
   * @return the updated student.
   */
  public StudentGroup update(String id, StudentGroupRequestDto groupRequest) {
    StudentGroup group = groupRequest.map();
    group.setId(id);
    return this.repository.save(group);
  }

  /**
   * Adds students to a student group.
   *
   * @param id         the ID of the student group.
   * @param studentIds the IDs of students which are to be added to the student group.
   */
  public StudentGroup addStudents(String id, List<String> studentIds) {
    StudentGroup studentGroup = getById(id);
    Set<Student> studentsInGroup = new HashSet<>(studentGroup.getStudents());
    studentsInGroup.addAll(studentIds.stream()
        .map(studentId -> studentRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST))).toList());
    studentGroup.setStudents(new ArrayList<Student>(studentsInGroup));
    return this.repository.save(studentGroup);
  }

  /**
   * Removes students from a student group.
   *
   * @param id         the ID of the student group.
   * @param studentIds the IDs of students which are to be removed.
   */
  public void removeStudents(String id, List<String> studentIds) {
    StudentGroup studentGroup = getById(id);
    List<Student> filteredStudents = studentGroup.getStudents().stream()
        .filter(student -> !studentIds.contains(student.getId())).toList();
    studentGroup.setStudents(new ArrayList<Student>(filteredStudents)); //make list mutable
    this.repository.save(studentGroup);
  }

  /**
   * Deletes the student group with the given ID.
   *
   * @param id the ID of the student which is to be deleted.
   * @throws ResponseStatusException is thrown if the given ID is not present in the database.
   */
  public void delete(String id) {
    var group = this.repository.findById(id);
    if (group.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(group.get());
  }
}
