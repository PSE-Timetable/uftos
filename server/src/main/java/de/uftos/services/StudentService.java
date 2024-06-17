package de.uftos.services;

import de.uftos.entities.Student;
import de.uftos.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service for accessing the student database entity.
 */
@Service
public class StudentService {
  private final StudentRepository repository;

  /**
   * Creates a student service.
   * @param repository The repository for accessing the student entity.
   */
  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the student entity.
   * @param pageable contains the parameters for the page.
   * @return the page of the entries fitting the parameters.
   */
  public Page<Student> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  /**
   * Gets a student from their ID.
   * @param id the ID of the student.
   * @return the student with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a coresponding student.
   */
  public Student getById(String id) {
    var student = this.repository.findById(id);

    return student.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new student in the database.
   * @param student the student which is to be created.
   * @return the updated student including the ID which has been assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the student parameter
   *         is already present in the database.
   */
  public Student create(Student student) {
    if (student.getId() != null && this.repository.findById(student.getId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return this.repository.save(student);
  }

  /**
   * Updates the student with the given ID.
   * @param id the ID of the student which is to be updated.
   * @param student the updated student information.
   * @return the updated student.
   */
  public Student update(String id, Student student) {
    student.setId(id);

    return this.repository.save(student);
  }

  /**
   * Deletes the student with the given ID.
   * @param id the ID of the student which is to be deleted.
   */
  public void delete(String id) {
    var student = this.repository.findById(id);
    if (student.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(student.get());
  }
}
