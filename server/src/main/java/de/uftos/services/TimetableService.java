package de.uftos.services;

import de.uftos.dto.requestdtos.TimetableRequestDto;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.GradeProblemDto;
import de.uftos.dto.solver.LessonProblemDto;
import de.uftos.dto.solver.ResourceProblemDto;
import de.uftos.dto.solver.RoomProblemDto;
import de.uftos.dto.solver.StudentGroupProblemDto;
import de.uftos.dto.solver.StudentProblemDto;
import de.uftos.dto.solver.SubjectProblemDto;
import de.uftos.dto.solver.TagProblemDto;
import de.uftos.dto.solver.TeacherProblemDto;
import de.uftos.dto.solver.TimeslotProblemDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.entities.ConstraintArgument;
import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import de.uftos.entities.Curriculum;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.LessonsCount;
import de.uftos.entities.Room;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.Timetable;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.repositories.database.TimetableRepository;
import de.uftos.repositories.solver.SolverRepository;
import de.uftos.repositories.ucdl.UcdlRepository;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /timetables endpoint.
 */
@Service
public class TimetableService {
  private final TimetableRepository timetableRepository;
  private final CurriculumRepository curriculumRepository;
  private final ConstraintSignatureRepository constraintSignatureRepository;
  private final GradeRepository gradeRepository;
  private final LessonRepository lessonRepository;
  private final RoomRepository roomRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final StudentRepository studentRepository;
  private final SubjectRepository subjectRepository;
  private final TagRepository tagRepository;
  private final TeacherRepository teacherRepository;
  private final TimeslotRepository timeslotRepository;
  private final UcdlRepository ucdlRepository;
  private final SolverRepository solverRepository;
  private final ServerRepository serverRepository;

  /**
   * Creates a timetable service.
   *
   * @param timetableRepository           the repository for accessing the timetable table.
   * @param curriculumRepository          the repository for accessing the curriculum table.
   * @param constraintSignatureRepository the repository for accessing the constraint signature table.
   * @param gradeRepository               the repository for accessing the grade table.
   * @param lessonRepository              the repository for accessing the lesson table.
   * @param roomRepository                the repository for accessing the room table.
   * @param studentGroupRepository        the repository for accessing the student group table.
   * @param studentRepository             the repository for accessing the student table.
   * @param subjectRepository             the repository for accessing the subject table.
   * @param tagRepository                 the repository for accessing the tag table.
   * @param teacherRepository             the repository for accessing the teacher table.
   * @param timeslotRepository            the repository for accessing the timeslot table.
   * @param ucdlRepository                the repository for parsing constraints.
   * @param solverRepository              the repository for solving a timetable instance.
   * @param serverRepository              the repository for accessing the server table.
   */
  @Autowired
  public TimetableService(TimetableRepository timetableRepository,
                          CurriculumRepository curriculumRepository,
                          ConstraintSignatureRepository constraintSignatureRepository,
                          GradeRepository gradeRepository,
                          LessonRepository lessonRepository,
                          RoomRepository roomRepository,
                          StudentGroupRepository studentGroupRepository,
                          StudentRepository studentRepository,
                          SubjectRepository subjectRepository,
                          TagRepository tagRepository,
                          TeacherRepository teacherRepository,
                          TimeslotRepository timeslotRepository,
                          UcdlRepository ucdlRepository,
                          SolverRepository solverRepository,
                          ServerRepository serverRepository
  ) {
    this.timetableRepository = timetableRepository;
    this.curriculumRepository = curriculumRepository;
    this.constraintSignatureRepository = constraintSignatureRepository;
    this.gradeRepository = gradeRepository;
    this.lessonRepository = lessonRepository;
    this.roomRepository = roomRepository;
    this.studentGroupRepository = studentGroupRepository;
    this.studentRepository = studentRepository;
    this.subjectRepository = subjectRepository;
    this.tagRepository = tagRepository;
    this.teacherRepository = teacherRepository;
    this.timeslotRepository = timeslotRepository;
    this.ucdlRepository = ucdlRepository;
    this.solverRepository = solverRepository;
    this.serverRepository = serverRepository;
  }

  /**
   * Gets a page of entries of the timetable table.
   *
   * @param pageable contains the parameters for the page.
   * @param name     the name filter.
   * @return the page of entries fitting the parameters.
   */
  public Page<Timetable> get(Pageable pageable, Optional<String> name) {
    return this.timetableRepository.findAll(pageable);
  }

  /**
   * Gets a timetable from their ID.
   *
   * @param id the ID of the timetable.
   * @return the timetable with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding timetable.
   */
  public Timetable getById(String id) {
    var timetable = this.timetableRepository.findById(id);

    return timetable.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  /**
   * Creates a new timetable in the database.
   *
   * @param timetable the information about the timetable which is to be created.
   * @return the created timetable which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the timetable parameter is
   *                                 already present in the database.
   */
  public Timetable create(TimetableRequestDto timetable) throws ResponseStatusException {
    //todo: make consumer do stuff and change returning of this method (currently returns once the solver is done)
    Consumer<TimetableSolutionDto> solverFinishedEvent = (solution) -> {
    };
    Timetable timetableEntity = timetable.map();
    timetableRepository.save(timetableEntity);
    try {
      TimetableProblemDto problemInstance = getProblemInstance(timetableEntity);
      return getSolution(solverRepository.solve(problemInstance, solverFinishedEvent).get());
    } catch (InterruptedException | ExecutionException | BadRequestException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  private TimetableProblemDto getProblemInstance(Timetable timetable) throws BadRequestException {
    List<GradeProblemDto> grades = getGrades();

    List<LessonProblemDto> lessons = getLessons(timetable);
    List<RoomProblemDto> rooms = getRooms(timetable);
    List<StudentGroupProblemDto> studentGroups = getStudentGroups(timetable);
    List<StudentProblemDto> students = getStudents();
    List<SubjectProblemDto> subjects = getSubjects(timetable);
    List<TagProblemDto> tags = getTags();
    List<TeacherProblemDto> teachers = getTeachers(timetable);
    List<TimeslotProblemDto> timeslots = getTimeslots(timetable);
    List<ConstraintDefinitionDto> definitions;
    try {
      definitions = ucdlRepository.getConstraints().values().stream().toList();
    } catch (ParseException | IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    HashMap<String, ResourceProblemDto> resources = new HashMap<>();


    for (GradeProblemDto grade : grades) {
      resources.put(grade.id(), grade);
    }
    for (RoomProblemDto room : rooms) {
      resources.put(room.id(), room);
    }
    for (StudentGroupProblemDto studentGroup : studentGroups) {
      resources.put(studentGroup.id(), studentGroup);
    }
    for (StudentProblemDto student : students) {
      resources.put(student.id(), student);
    }
    for (SubjectProblemDto subject : subjects) {
      resources.put(subject.id(), subject);
    }
    for (TagProblemDto tag : tags) {
      resources.put(tag.id(), tag);
    }
    for (TeacherProblemDto teacher : teachers) {
      resources.put(teacher.id(), teacher);
    }
    for (TimeslotProblemDto timeslot : timeslots) {
      resources.put(timeslot.id(), timeslot);
    }

    List<ConstraintInstanceDto> instances = getConstraintInstances(resources);

    return new TimetableProblemDto(grades, lessons, rooms, studentGroups, students, subjects, tags,
        teachers, timeslots, definitions,
        instances);
  }

  private List<GradeProblemDto> getGrades() {
    List<GradeProblemDto> grades = new ArrayList<>();
    for (Grade grade : gradeRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : grade.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> studentGroupIds = new ArrayList<>();
      for (StudentGroup studentGroup : grade.getStudentGroups()) {
        studentGroupIds.add(studentGroup.getId());
      }
      grades.add(new GradeProblemDto(grade.getId(), tagIds, studentGroupIds));
    }
    return grades;
  }

  private List<LessonProblemDto> getLessons(Timetable timetable) {
    //initializing new Lessons
    List<Curriculum> curriculums = curriculumRepository.findAll();
    for (StudentGroup studentGroup : studentGroupRepository.findAll()) {
      for (Curriculum curriculum : curriculums) {
        if (curriculum.getGrade() != studentGroup.getGrades().getFirst()) {
          continue;
        }
        for (LessonsCount lessonsCount : curriculum.getLessonsCounts()) {
          if (!studentGroup.getSubjects().contains(lessonsCount.getSubject())) {
            continue;
          }
          for (int index = 0; index < lessonsCount.getCount(); index++) {
            Lesson lesson = new Lesson();
            lesson.setYear(serverRepository.findAll().getLast().getCurrentYear());
            lesson.setIndex(index);
            lesson.setStudentGroup(studentGroup);
            lesson.setSubject(lessonsCount.getSubject());
            lesson.setTeacher(teacherRepository.findAll().getFirst());
            lesson.setRoom(roomRepository.findAll().getFirst());
            lesson.setTimeslot(timeslotRepository.findAll().getFirst());
            lesson.setTimetable(timetable);

            lessonRepository.save(lesson);
          }
        }
        break;
      }
    }

    //creating list for solver
    List<LessonProblemDto> lessons = new ArrayList<>();
    for (Lesson lesson : lessonRepository.findAll().stream()
        .filter((lesson -> lesson.getTimetable() == timetable)).toList()) {
      lessons.add(
          new LessonProblemDto(lesson.getId(), lesson.getIndex(), lesson.getTeacher().getId(),
              lesson.getStudentGroup().getId(), lesson.getTimeslot().getId(),
              lesson.getSubject().getId(), lesson.getRoom().getId()));
    }
    return lessons;
  }

  private List<RoomProblemDto> getRooms(Timetable timetable) {
    List<RoomProblemDto> rooms = new ArrayList<>();
    for (Room room : roomRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : room.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (Lesson lesson : room.getLessons().stream()
          .filter(lesson -> lesson.getTimetable() == timetable).toList()) {
        lessonIds.add(lesson.getId());
      }
      rooms.add(new RoomProblemDto(room.getId(), tagIds, lessonIds));
    }
    return rooms;
  }

  private List<StudentGroupProblemDto> getStudentGroups(Timetable timetable) {
    List<StudentGroupProblemDto> studentGroups = new ArrayList<>();
    for (StudentGroup studentGroup : studentGroupRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : studentGroup.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (Lesson lesson : studentGroup.getLessons().stream()
          .filter(lesson -> lesson.getTimetable() == timetable).toList()) {
        lessonIds.add(lesson.getId());
      }
      List<String> studentIds = new ArrayList<>();
      for (Student student : studentGroup.getStudents()) {
        studentIds.add(student.getId());
      }
      studentGroups.add(new StudentGroupProblemDto(studentGroup.getId(),
          studentGroup.getGrades().getFirst().getId(), tagIds, lessonIds, studentIds));
    }
    return studentGroups;
  }

  private List<StudentProblemDto> getStudents() {
    List<StudentProblemDto> students = new ArrayList<>();
    for (Student student : studentRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : student.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> studentGroupIds = new ArrayList<>();
      for (StudentGroup studentGroup : student.getGroups()) {
        studentGroupIds.add(studentGroup.getId());
      }
      students.add(new StudentProblemDto(student.getId(), tagIds, studentGroupIds));
    }
    return students;
  }

  private List<SubjectProblemDto> getSubjects(Timetable timetable) {
    List<SubjectProblemDto> subjects = new ArrayList<>();
    for (Subject subject : subjectRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : subject.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (Lesson lesson : subject.getLessons().stream()
          .filter(lesson -> lesson.getTimetable() == timetable).toList()) {
        lessonIds.add(lesson.getId());
      }
      List<String> teacherIds = new ArrayList<>();
      for (Teacher teacher : subject.getTeachers()) {
        teacherIds.add(teacher.getId());
      }
      subjects.add(new SubjectProblemDto(subject.getId(), tagIds, lessonIds, teacherIds));
    }
    return subjects;
  }

  private List<TagProblemDto> getTags() {
    List<TagProblemDto> tags = new ArrayList<>();
    for (Tag tag : tagRepository.findAll()) {
      List<String> gradeIds = new ArrayList<>();
      for (Grade grade : tag.getGrades()) {
        gradeIds.add(grade.getId());
      }
      List<String> roomIds = new ArrayList<>();
      for (Room room : tag.getRooms()) {
        roomIds.add(room.getId());
      }
      List<String> studentIds = new ArrayList<>();
      for (Student student : tag.getStudents()) {
        studentIds.add(student.getId());
      }
      List<String> studentGroupIds = new ArrayList<>();
      for (StudentGroup studentGroup : tag.getStudentGroups()) {
        studentGroupIds.add(studentGroup.getId());
      }
      List<String> subjectIds = new ArrayList<>();
      for (Subject subject : tag.getSubjects()) {
        subjectIds.add(subject.getId());
      }
      List<String> teacherIds = new ArrayList<>();
      for (Teacher teacher : tag.getTeachers()) {
        teacherIds.add(teacher.getId());
      }
      List<String> timeslotIds = new ArrayList<>();
      for (Timeslot timeslot : tag.getTimeslots()) {
        timeslotIds.add(timeslot.getId());
      }
      tags.add(
          new TagProblemDto(tag.getId(), gradeIds, roomIds, studentIds, studentGroupIds, subjectIds,
              teacherIds, timeslotIds));
    }
    return tags;
  }

  private List<TeacherProblemDto> getTeachers(Timetable timetable) {
    List<TeacherProblemDto> teachers = new ArrayList<>();
    for (Teacher teacher : teacherRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : teacher.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (Lesson lesson : teacher.getLessons().stream()
          .filter(lesson -> lesson.getTimetable() == timetable).toList()) {
        lessonIds.add(lesson.getId());
      }
      List<String> subjectIds = new ArrayList<>();
      for (Subject subject : teacher.getSubjects()) {
        subjectIds.add(subject.getId());
      }
      teachers.add(new TeacherProblemDto(teacher.getId(), tagIds, lessonIds, subjectIds));
    }
    return teachers;
  }

  private List<TimeslotProblemDto> getTimeslots(Timetable timetable) {
    List<TimeslotProblemDto> timeslots = new ArrayList<>();
    for (Timeslot timeslot : timeslotRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : timeslot.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (Lesson lesson : timeslot.getLessons().stream()
          .filter(lesson -> lesson.getTimetable() == timetable).toList()) {
        lessonIds.add(lesson.getId());
      }
      timeslots.add(
          new TimeslotProblemDto(timeslot.getId(), timeslot.getDay().ordinal(), timeslot.getSlot(),
              tagIds, lessonIds));
    }
    return timeslots;
  }

  private List<ConstraintInstanceDto> getConstraintInstances(
      HashMap<String, ResourceProblemDto> resources) {
    List<ConstraintInstanceDto> instances = new ArrayList<>();

    for (ConstraintSignature signature : constraintSignatureRepository.findAll()) {
      for (ConstraintInstance instance : signature.getInstances()) {
        List<ResourceProblemDto> arguments = new ArrayList<>();
        for (ConstraintArgument argument : instance.getArguments()) {
          arguments.add(resources.get(argument.getValue()));
        }
        instances.add(
            new ConstraintInstanceDto(signature.getName(), instance.getType(), arguments));
      }
    }

    return instances;
  }

  private Timetable getSolution(TimetableSolutionDto solution) {
    if (solution.hardScore() < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          (-solution.hardScore()) + " hard constraints could not be satisfied");
    }

    HashMap<String, LessonProblemDto> lessons = new HashMap<>();

    for (LessonProblemDto lesson : solution.lessons()) {
      lessons.put(lesson.id(), lesson);
    }

    List<Lesson> lessonEntities = lessonRepository.findAllById(lessons.keySet());

    for (Lesson lessonEntity : lessonEntities) {
      LessonProblemDto lesson = lessons.get(lessonEntity.getId());

      Optional<Room> room = roomRepository.findById(lesson.roomId());
      Optional<Teacher> teacher = teacherRepository.findById(lesson.teacherId());
      Optional<Timeslot> timeslot = timeslotRepository.findById(lesson.timeslotId());

      if (room.isEmpty() || teacher.isEmpty() || timeslot.isEmpty()) {
        throw new IllegalStateException();
      }

      lessonEntity.setRoom(room.get());
      lessonEntity.setTeacher(teacher.get());
      lessonEntity.setTimeslot(timeslot.get());

      lessonRepository.save(lessonEntity);
    }

    return lessonEntities.getFirst().getTimetable();
  }

  /**
   * Deletes the timetable with the given ID.
   *
   * @param id the ID of the timetable which is to be deleted.
   * @throws ResponseStatusException is thrown if no timetable exists with the given ID.
   */
  public void delete(String id) {
    var timetable = this.timetableRepository.findById(id);
    if (timetable.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.timetableRepository.delete(timetable.get());
  }
}
