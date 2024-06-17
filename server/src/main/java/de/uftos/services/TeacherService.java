package de.uftos.services;

import de.uftos.entities.Teacher;
import de.uftos.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TeacherService {
  private final TeacherRepository repository;

  @Autowired
  public TeacherService(TeacherRepository repository) {
    this.repository = repository;
  }

  public Page<Teacher> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public Teacher getById(long id) {
    var teacher = this.repository.findById(id);

    return teacher.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public Teacher create(Teacher teacher) {
    if (this.repository.findById(teacher.getId()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    return this.repository.save(teacher);
  }

  public Teacher update(long id, Teacher teacher) {
    teacher.setId(id);

    return this.repository.save(teacher);
  }

  public void delete(long id) {
    var teacher = this.repository.findById(id);
    if (teacher.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(teacher.get());
  }
}
