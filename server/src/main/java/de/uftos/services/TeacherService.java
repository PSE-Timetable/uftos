package de.uftos.services;

import de.uftos.dto.LessonResponseDto;
import de.uftos.dto.TeacherRequestDto;
import de.uftos.entities.Teacher;
import de.uftos.repositories.TeacherRepository;
import java.util.List;
import java.util.Optional;
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

  public Teacher getById(String id) {
    Optional<Teacher> teacher = this.repository.findById(id);

    return teacher.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public List<LessonResponseDto> getLessonsById(String id) {
    Teacher teacher = this.getById(id);
    // TODO
    return null;
  }

  public Teacher create(TeacherRequestDto teacher) {
    return this.repository.save(teacher.map());
  }

  public Teacher update(String id, TeacherRequestDto teacherRequest) {
    Teacher teacher = teacherRequest.map();
    teacher.setId(id);

    return this.repository.save(teacher);
  }

  public void delete(String id) {
    var teacher = this.repository.findById(id);
    if (teacher.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(teacher.get());
  }
}
