package de.uftos.services;

import de.uftos.dto.requestdtos.TeacherRequestDto;
import de.uftos.dto.responsedtos.LessonResponseDto;
import de.uftos.entities.Lesson;
import de.uftos.entities.Teacher;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.TeacherRepository;
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
   * @param pageable contains the parameters for the page.
   * @param search   the text to search across first and last name as well as the acronym.
   * @param subjects the subjects filter.
   * @param tags     the tags filter.
   * @return the page of entries fitting the parameters.
   */
  public Page<Teacher> get(Pageable pageable, Optional<String> search,
                           Optional<String[]> subjects, Optional<String[]> tags) {
    Specification<Teacher> spec = new SpecificationBuilder<Teacher>()
        .search(search)
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

    return teacher.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Could not find a teacher with this id"));
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
   * @throws ResponseStatusException is thrown if the first name, last name or the acronym of the teacher is blank.
   */
  public Teacher create(TeacherRequestDto teacher) {
    if (teacher.firstName().isBlank() || teacher.lastName().isBlank() 
        || teacher.acronym().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The first name, last name or the acronym of the teacher is blank.");
    }
    return this.repository.save(teacher.map());
  }

  /**
   * Updates the teacher with the given ID.
   *
   * @param id             the ID of the teacher which is to be updated.
   * @param teacherRequest the updated teacher information.
   * @return the updated teacher.
   * @throws ResponseStatusException is thrown if the first name, last name or the acronym of the teacher is blank.
   */
  public Teacher update(String id, TeacherRequestDto teacherRequest) {
    if (teacherRequest.firstName().isBlank() || teacherRequest.lastName().isBlank()
        || teacherRequest.acronym().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The first name, last name or acronym of the teacher is blank.");
    }
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
    Optional<Teacher> teacher = this.repository.findById(id);
    if (teacher.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Could not find a teacher with this id");
    }

    this.repository.delete(teacher.get());
  }

  /**
   * Deletes the teachers with the given IDs.
   *
   * @param ids the IDs of the teacher which are to be deleted.
   * @throws ResponseStatusException is thrown if no teacher exists with the given ID.
   */
  public void deleteTeachers(String[] ids) {
    List<String> teacherIds = Arrays.asList(ids);
    List<Teacher> teachers = this.repository.findAllById(teacherIds);
    if (teachers.size() != teacherIds.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find a teacher with this id!");
    }

    this.repository.deleteAll(teachers);
  }
}
