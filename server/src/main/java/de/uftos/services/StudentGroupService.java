package de.uftos.services;

import de.uftos.builders.SpecificationBuilder;
import de.uftos.dto.LessonResponseDto;
import de.uftos.dto.StudentAndGroup;
import de.uftos.dto.StudentGroupRequestDto;
import de.uftos.entities.StudentGroup;
import de.uftos.repositories.database.StudentGroupRepository;
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
 * The service providing the logic of the /student-groups endpoint.
 */
@Service
public class StudentGroupService {
  private final StudentGroupRepository repository;

  /**
   * Creates a student group service.
   *
   * @param repository the repository for accessing the student group table.
   */
  @Autowired
  public StudentGroupService(StudentGroupRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the student group table.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @return the page of the entries fitting the parameters.
   */
  public Page<StudentGroup> get(Pageable pageable, Optional<String> name) {
    Specification<StudentGroup> spec = new SpecificationBuilder<StudentGroup>()
            .optionOrEquals(name, "name")
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
    Optional<StudentGroup> group = this.repository.findById(id);
    return group.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Gets the information about the lessons that the student group attends.
   *
   * @param id the ID of the student group.
   * @return a LessonResponseDto containing information about the lessons.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding student group.
   */
  public LessonResponseDto getLessonsById(String id) {
    StudentGroup studentGroup = this.getById(id);
    return LessonResponseDto.createResponseDtoFromLessons(studentGroup.getLessons());
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
    this.repository.addStudentsToGroups(
        studentIds.stream().map((studentId) -> new StudentAndGroup(studentId, id)).toList());
    return this.getById(id);
  }

  /**
   * Removes students from a student group.
   *
   * @param id         the ID of the student group.
   * @param studentIds the IDs of students which are to be removed.
   */
  public void removeStudents(String id, List<String> studentIds) {
    this.repository.removeStudentsFromGroup(id, studentIds);
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
