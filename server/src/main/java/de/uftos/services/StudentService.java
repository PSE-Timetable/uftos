package de.uftos.services;

import de.uftos.dto.StudentRequestDto;
import de.uftos.entities.Student;
import de.uftos.repositories.StudentRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {
  private final StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public Page<Student> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Student getById(String id) {
    Optional<Student> student = this.repository.findById(id);

    return student.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Student create(StudentRequestDto student) {
    return this.repository.save(student.map());
  }

  public Student update(String id, StudentRequestDto studentRequest) {
    Student student = studentRequest.map();
    student.setId(id);

    return this.repository.save(student);
  }

  public void delete(String id) {
    var student = this.repository.findById(id);
    if (student.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(student.get());
  }
}
