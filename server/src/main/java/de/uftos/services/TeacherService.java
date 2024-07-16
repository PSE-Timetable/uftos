package de.uftos.services;

import de.uftos.dto.LessonResponseDto;
import de.uftos.dto.TeacherRequestDto;
import de.uftos.entities.Lesson;
import de.uftos.entities.Teacher;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.ArrayList;
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
 * The service providing the logic of the /teachers endpoint.
 */
@Service
public class TeacherService {
  private final TeacherRepository repository;
  private final ServerRepository serverRepository;

  /**
   * Creates a teacher service.
   *
   * @param repository the repository for accessing the teacher table.
   */
  @Autowired
  public TeacherService(TeacherRepository repository, ServerRepository serverRepository) {
    this.repository = repository;
    this.serverRepository = serverRepository;
  }

  /**
   * Gets a page of entries of the teacher entity.
   *
   * @param pageable  contains the parameters for the page.
   * @param firstName the first name filter.
   * @param lastName  the last name filter.
   * @param acronym   the acronym filter.
   * @param subjects  the subjects filter.
   * @param tags      the tags filter.
   * @return the page of entries fitting the parameters.
   */
  public Page<Teacher> get(Pageable pageable, Optional<String> firstName,
                           Optional<String> lastName, Optional<String> acronym,
                           Optional<String[]> subjects, Optional<String[]> tags) {
    Specification<Teacher> spec = new SpecificationBuilder<Teacher>()
        .optionalOrEquals(firstName, "firstName")
        .optionalOrEquals(lastName, "lastName")
        .optionalOrEquals(acronym, "acronym")
        .optionalAndJoinIn(subjects, "subjects", "id")
        .optionalAndJoinIn(tags, "tags", "id")
        .build();
    return this.repository.findAll(spec, pageable);
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

  /**
   * Gets the information about the lessons that the teacher teaches.
   *
   * @param id the ID of the teacher.
   * @return a LessonResponseDto containing information about the lessons.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding teacher.
   */
  public LessonResponseDto getLessonsById(String id) {
    Teacher teacher = getById(id);
    List<Lesson> lessons = new ArrayList<>(teacher.getLessons());
    lessons.removeIf(lesson -> !lesson.getYear().equals(
        serverRepository.findAll().getFirst().getCurrentYear()));
    return LessonResponseDto.createResponseDtoFromLessons(lessons);
  }

  /**
   * Creates a new teacher in the database.
   *
   * @param teacher the information about the teacher which is to be created.
   * @return the created teacher which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the teacher parameter is
   *                                 already present in the database.
   */
  public Teacher create(TeacherRequestDto teacher) {
    return this.repository.save(teacher.map());
  }

  /**
   * Updates the teacher with the given ID.
   *
   * @param id             the ID of the teacher which is to be updated.
   * @param teacherRequest the updated teacher information.
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
