package de.uftos.services;

import de.uftos.dto.requestdtos.StudentRequestDto;
import de.uftos.entities.Student;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.utils.SpecificationBuilder;
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

  /**
   * Creates a student service.
   *
   * @param repository the repository for accessing the student table.
   */
  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the student table.
   *
   * @param pageable  contains the parameters for the page.
   * @param firstName the first name filter.
   * @param lastName  the last name filter.
   * @param groups    the student groups filter.
   * @param tags      the tags filter.
   * @return the page of the entries fitting the parameters.
   */
  public Page<Student> get(Pageable pageable, Optional<String> firstName,
                           Optional<String> lastName, Optional<String[]> groups, Optional<String[]> tags) {
    if (!groups.isPresent()) {
      System.out.println("no groups test");
    }
    if (groups.isPresent()) {
      System.out.println("test with groups:");
      for (String id : groups.get()) {
        System.out.println(id);
      }
    }

    Specification<Student> spec = new SpecificationBuilder<Student>()
        .optionalOrLike(firstName, "firstName")
        .optionalOrLike(lastName, "lastName")
        .optionalAndIn(groups, "groups")
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

    return student.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new student in the database.
   *
   * @param student the student which is to be created.
   * @return the updated student which includes the ID that has been assigned.
   * @throws ResponseStatusException is thrown if the first or last name of the student is blank.
   */
  public Student create(StudentRequestDto student) {
    if (student.firstName().isBlank() || student.lastName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The first or last name of the student is blank.");
    }
    Student result = this.repository.save(student.map());
    System.out.println("Student " + result.getFirstName() + " has groups " + result.getGroups());
    return result;
    //return this.repository.save(student.map());
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
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The first or last name of the student is blank.");
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
    var student = this.repository.findById(id);
    if (student.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(student.get());
  }
}
