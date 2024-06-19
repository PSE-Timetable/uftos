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

/**
 * The service for accessing the teacher database entry.
 */
@Service
public class TeacherService {
  private final TeacherRepository repository;

  /**
   * Creates a teacher service.
   *
   * @param repository the repository for accessing the teacher entity.
   */
  @Autowired
  public TeacherService(TeacherRepository repository) {
    this.repository = repository;
  }

  /**
   * Gets a page of entries of the teacher entity.
   *
   * @param pageable contains the parameters for the page.
   * @return the page of entries fitting the parameters.
   */
  public Page<Teacher> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  /**
   * Gets a teacher from their ID.
   *
   * @param id the ID of the teacher.
   * @return the teacher with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding teacher.
   */
  public Teacher getById(String id) {
    Optional<Teacher> teacher = this.repository.findById(id);

    return teacher.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }


  public List<LessonResponseDto> getLessonsById(String id) {
    Teacher teacher = this.getById(id);
    // TODO
    return null;
  }

  /**
   * Creates a new teacher in the database.
   *
   * @param teacher the teacher which is to be created.
   * @return the updated teacher which includes
   * @throws ResponseStatusException is thrown if the ID defined in the teacher parameter is
   *         already present in the database.
   */
  public Teacher create(TeacherRequestDto teacher) {
    return this.repository.save(teacher.map());
  }

  /**
   * Updates the teacher with the given ID.
   *
   * @param id the ID of the teacher which is to be updated.
   * @param teacher the updated teacher information
   * @return the updated teacher.
   */
  public Teacher update(String id, TeacherRequestDto teacherRequest) {
    Teacher teacher = teacherRequest.map();
    teacher.setId(id);

    return this.repository.save(teacher);
  }

  /**
   * Deletes the teacher with the given ID.
   *
   * @param id the ID of the teacher which is to be deleted.
   * @throws ResponseStatusException is thrown if no teacher exists with the given ID.
   */
  public void delete(String id) {
    var teacher = this.repository.findById(id);
    if (teacher.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(teacher.get());
  }
}
