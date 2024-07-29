package de.uftos.services;

import de.uftos.dto.TimetableRequestDto;
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
import de.uftos.entities.StudentGroup;
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
  public Timetable create(TimetableRequestDto timetable) throws BadRequestException {
    Timetable timetableEntity = timetable.map();
    timetableRepository.save(timetableEntity);
    TimetableProblemDto problemInstance = getProblemInstance(timetableEntity);
    try {
      return getSolution(solverRepository.solve(problemInstance).get());
    } catch (InterruptedException | ExecutionException e) {
      throw new BadRequestException(e);
    }
  }

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
    List<ConstraintDefinitionDto> definitions = null;
    try {
      definitions = ucdlRepository.getConstraints().values().stream().toList();
    } catch (ParseException | IOException e) {
      throw new BadRequestException(e);
    }

    HashMap<String, ResourceProblemDto> resources = new HashMap<>();


    for (GradeProblemDto grade : grades) {
      resources.put(grade.id(), grade);
    }
    //todo: put other resource-dtos inside

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
        if (curriculum.getGrade() == studentGroup.getGrades().getFirst()) {
          for (LessonsCount lessonsCount : curriculum.getLessonsCounts()) {
            if (studentGroup.getSubjects().contains(lessonsCount.getSubject())) {
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
          }
          break;
        }
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

  //todo: implement ProblemDto-creation
  private List<RoomProblemDto> getRooms(Timetable timetable) {
    return null;
  }

  private List<StudentGroupProblemDto> getStudentGroups(Timetable timetable) {
    return null;
  }

  private List<StudentProblemDto> getStudents() {
    return null;
  }

  private List<SubjectProblemDto> getSubjects(Timetable timetable) {
    return null;
  }

  private List<TagProblemDto> getTags() {
    return null;
  }

  private List<TeacherProblemDto> getTeachers(Timetable timetable) {
    return null;
  }

  private List<TimeslotProblemDto> getTimeslots(Timetable timetable) {
    return null;
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
