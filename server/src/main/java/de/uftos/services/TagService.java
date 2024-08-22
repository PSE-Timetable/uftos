package de.uftos.services;

import de.uftos.dto.requestdtos.TagRequestDto;
import de.uftos.entities.Grade;
import de.uftos.entities.Room;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.utils.SpecificationBuilder;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /tags endpoint.
 */
@Service
public class TagService {
  private final TagRepository repository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final RoomRepository roomRepository;
  private final SubjectRepository subjectRepository;
  private final GradeRepository gradeRepository;
  private final TimeslotRepository timeslotRepository;

  /**
   * Creates a tag service.
   *
   * @param repository             the repository for accessing the tag table.
   * @param studentRepository      the repository for accessing the student table.
   * @param teacherRepository      the repository for accessing the teacher table.
   * @param studentGroupRepository the repository for accessing the student group table.
   * @param roomRepository         the repository for accessing the room table.
   * @param subjectRepository      the repository for accessing the subject table.
   * @param gradeRepository        the repository for accessing the grade table.
   * @param timeslotRepository     the repository for accessing the timeslot table.
   */
  @Autowired
  public TagService(TagRepository repository, StudentRepository studentRepository,
                    TeacherRepository teacherRepository,
                    StudentGroupRepository studentGroupRepository, RoomRepository roomRepository,
                    SubjectRepository subjectRepository, GradeRepository gradeRepository,
                    TimeslotRepository timeslotRepository) {
    this.repository = repository;
    this.studentRepository = studentRepository;
    this.teacherRepository = teacherRepository;
    this.studentGroupRepository = studentGroupRepository;
    this.roomRepository = roomRepository;
    this.subjectRepository = subjectRepository;
    this.gradeRepository = gradeRepository;
    this.timeslotRepository = timeslotRepository;
  }

  /**
   * Gets a page of entries of the tag table.
   *
   * @param search the search filter.
   * @return the page of entries fitting the parameters.
   */
  public List<Tag> get(Sort sort, Optional<String> search) {
    Specification<Tag> specification = new SpecificationBuilder<Tag>()
        .search(search)
        .build();

    return this.repository.findAll(specification, sort);
  }

  /**
   * Gets a tag from their ID.
   *
   * @param id the ID of the tag.
   * @return the tag with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding tag.
   */
  public Tag getById(String id) {
    var tag = this.repository.findById(id);

    return tag.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new tag in the database.
   *
   * @param tag the information about the tag which is to be created.
   * @return the created tag which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the name of the tag is blank.
   */
  public Tag create(TagRequestDto tag) {
    if (tag.tagName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The name of the tag is blank.");
    }
    return this.repository.save(tag.map());
  }

  /**
   * Updates the tag with the given ID.
   *
   * @param id         the ID of the tag which is to be updated.
   * @param tagRequest the updated tag information.
   * @return the updated tag.
   * @throws ResponseStatusException is thrown if the name of the tag is blank.
   */
  public Tag update(String id, TagRequestDto tagRequest) {
    if (tagRequest.tagName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The name of the tag is blank.");
    }
    Tag tag = tagRequest.map();
    tag.setId(id);

    return this.repository.save(tag);
  }

  /**
   * Deletes the tag with the given ID.
   *
   * @param id the ID of the tag which is to be deleted.
   * @throws ResponseStatusException is thrown if no tag exists with the given ID.
   */
  public void delete(String id) {
    Optional<Tag> tag = this.repository.findById(id);
    if (tag.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    List<Student> students = studentRepository.findByTags(tag.get());
    for (Student student : students) {
      student.getTags().removeIf(tag1 -> tag1.getId().equals(id));
    }
    studentRepository.saveAll(students);

    List<Teacher> teachers = teacherRepository.findByTags(tag.get());
    for (Teacher teacher : teachers) {
      teacher.getTags().removeIf(tag1 -> tag1.getId().equals(id));
    }
    teacherRepository.saveAll(teachers);

    List<StudentGroup> studentGroups = studentGroupRepository.findByTags(tag.get());
    for (StudentGroup group : studentGroups) {
      group.getTags().removeIf(tag1 -> tag1.getId().equals(id));
    }
    studentGroupRepository.saveAll(studentGroups);

    List<Room> rooms = roomRepository.findByTags(tag.get());
    for (Room room : rooms) {
      room.getTags().removeIf(tag1 -> tag1.getId().equals(id));
    }
    roomRepository.saveAll(rooms);

    List<Subject> subjects = subjectRepository.findByTags(tag.get());
    for (Subject subject : subjects) {
      subject.getTags().removeIf(tag1 -> tag1.getId().equals(id));
    }
    subjectRepository.saveAll(subjects);

    List<Grade> grades = gradeRepository.findByTags(tag.get());
    for (Grade grade : grades) {
      grade.getTags().removeIf(tag1 -> tag1.getId().equals(id));
    }
    gradeRepository.saveAll(grades);

    List<Timeslot> timeslots = timeslotRepository.findByTags(tag.get());
    for (Timeslot timeslot : timeslots) {
      timeslot.getTags().removeIf(tag1 -> tag1.getId().equals(id));
    }
    timeslotRepository.saveAll(timeslots);

    this.repository.delete(tag.get());
  }
}
