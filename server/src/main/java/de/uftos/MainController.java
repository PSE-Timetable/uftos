package de.uftos;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.GradeProblemDto;
import de.uftos.dto.solver.LessonProblemDto;
import de.uftos.dto.solver.ResourceProblemDto;
import de.uftos.dto.solver.RewardPenalize;
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
import de.uftos.entities.Grade;
import de.uftos.entities.Room;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.repositories.solver.SolverRepositoryImpl;
import de.uftos.repositories.ucdl.parser.PredefinedConstraint;
import de.uftos.repositories.ucdl.parser.UcdlParser;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainController {
  private static Future<TimetableSolutionDto> solution = null;


  private final GradeRepository gradeRepository;
  private final SubjectRepository subjectRepository;
  private final RoomRepository roomRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final CurriculumRepository curriculumRepository;
  private final TagRepository tagRepository;
  private final TimeslotRepository timeslotRepository;

  public MainController(
      GradeRepository gradeRepository,
      SubjectRepository subjectRepository,
      RoomRepository roomRepository,
      StudentGroupRepository studentGroupRepository,
      StudentRepository studentRepository,
      TeacherRepository teacherRepository,
      CurriculumRepository curriculumRepository,
      TagRepository tagRepository,
      TimeslotRepository timeslotRepository
  ) {
    this.gradeRepository = gradeRepository;
    this.subjectRepository = subjectRepository;
    this.roomRepository = roomRepository;
    this.studentGroupRepository = studentGroupRepository;
    this.studentRepository = studentRepository;
    this.teacherRepository = teacherRepository;
    this.curriculumRepository = curriculumRepository;
    this.tagRepository = tagRepository;
    this.timeslotRepository = timeslotRepository;
  }

  @GetMapping()
  public ResponseEntity<String> main() {
    return ResponseEntity.ok("MAIN");
  }

  @GetMapping("/start")
  public Future<TimetableSolutionDto> start() {

    List<GradeProblemDto> grades = new ArrayList<>();
    for (Grade grade : gradeRepository.findAll()) {
      List<String> studentGroupIds = new ArrayList<>();
      for (StudentGroup studentGroup : grade.getStudentGroups()) {
        studentGroupIds.add(studentGroup.getId());
      }
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : grade.getTags()) {
        tagIds.add(tag.getId());
      }
      grades.add(new GradeProblemDto(grade.getId(), tagIds, studentGroupIds));
    }

    List<LessonProblemDto> lessons = new ArrayList<>();
    int id = 0;
    for (Subject subject : subjectRepository.findAll()) {
      for (StudentGroup studentGroup : studentGroupRepository.findAll()) {
        for (int index = 0; index < 3; index++) {
          lessons.add(
              new LessonProblemDto(
                  "LESSON_" + id++,
                  index,
                  null,
                  studentGroup.getId(),
                  null,
                  subject.getId(),
                  null
              ));
        }
      }
    }

    List<RoomProblemDto> rooms = new ArrayList<>();
    for (Room room : roomRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : room.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (LessonProblemDto lesson : lessons) {
        if (room.getId().equals(lesson.roomId())) {
          lessonIds.add(lesson.id());
        }
      }
      rooms.add(new RoomProblemDto(room.getId(), tagIds, lessonIds));
    }

    List<StudentGroupProblemDto> studentGroups = new ArrayList<>();
    for (StudentGroup studentGroup : studentGroupRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : studentGroup.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (LessonProblemDto lesson : lessons) {
        if (studentGroup.getId().equals(lesson.studentGroupId())) {
          lessonIds.add(lesson.id());
        }
      }
      List<String> studentIds = new ArrayList<>();
      for (Student student : studentGroup.getStudents()) {
        studentIds.add(student.getId());
      }
      studentGroups.add(new StudentGroupProblemDto(studentGroup.getId(),
          studentGroup.getGrades().getFirst().getId(), tagIds, lessonIds, studentIds));
    }

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

    List<SubjectProblemDto> subjects = new ArrayList<>();
    for (Subject subject : subjectRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : subject.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (LessonProblemDto lesson : lessons) {
        if (subject.getId().equals(lesson.subjectId())) {
          lessonIds.add(lesson.id());
        }
      }
      List<String> teacherIds = new ArrayList<>();
      for (Teacher teacher : subject.getTeachers()) {
        teacherIds.add(teacher.getId());
      }
      subjects.add(new SubjectProblemDto(subject.getId(), tagIds, lessonIds, teacherIds));
    }

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

    List<TeacherProblemDto> teachers = new ArrayList<>();
    for (Teacher teacher : teacherRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : teacher.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (LessonProblemDto lesson : lessons) {
        if (teacher.getId().equals(lesson.teacherId())) {
          lessonIds.add(lesson.id());
        }
      }
      List<String> subjectIds = new ArrayList<>();
      for (Subject subject : teacher.getSubjects()) {
        subjectIds.add(subject.getId());
      }
      teachers.add(new TeacherProblemDto(teacher.getId(), tagIds, lessonIds, subjectIds));
    }

    List<TimeslotProblemDto> timeslots = new ArrayList<>();
    for (Timeslot timeslot : timeslotRepository.findAll()) {
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : timeslot.getTags()) {
        tagIds.add(tag.getId());
      }
      List<String> lessonIds = new ArrayList<>();
      for (LessonProblemDto lesson : lessons) {
        if (timeslot.getId().equals(lesson.timeslotId())) {
          lessonIds.add(lesson.id());
        }
      }
      timeslots.add(
          new TimeslotProblemDto(timeslot.getId(), timeslot.getDay().ordinal(), timeslot.getSlot(),
              tagIds, lessonIds));
    }

    List<ConstraintInstanceDto> instances = new ArrayList<>();

    TimetableProblemDto timetable =
        new TimetableProblemDto(
            grades,
            lessons,
            rooms,
            studentGroups,
            students,
            subjects,
            tags,
            teachers,
            timeslots,
            getDefinitions(),
            instances
        );

    setInstances(timetable);


    solution = new SolverRepositoryImpl().solve(timetable);
    return solution;
  }

  @GetMapping("/status")
  public Future<TimetableSolutionDto> status() {
    if (solution == null) {
      return null;
    }
    return solution;
  }

  @GetMapping("/view")
  public TimetableSolutionDto view() throws ExecutionException, InterruptedException {
    if (solution == null || !solution.isDone()) {
      return null;
    }
    return solution.get();
  }

  private List<ConstraintDefinitionDto> getDefinitions() {
    StringBuilder stringBuilder = new StringBuilder();
    for (PredefinedConstraint constraint : PredefinedConstraint.values()) {
      stringBuilder.append(constraint.getCode());
    }

    try {
      return UcdlParser.getDefinitions(stringBuilder.toString()).values().stream().toList();
    } catch (JsonProcessingException | ParseException e) {
      return new ArrayList<>();
    }
  }

  private void setInstances(TimetableProblemDto timetable) {
    List<ConstraintInstanceDto> instances = timetable.instances();

    for (LessonProblemDto lesson1 : timetable.lessons()) {
      for (LessonProblemDto lesson2 : timetable.lessons()) {
        List<ResourceProblemDto> params = List.of(lesson1, lesson2);
        instances.add(new ConstraintInstanceDto(PredefinedConstraint.TEACHER_COLLISION.getName(),
            RewardPenalize.HARD_PENALIZE, params));
        instances.add(new ConstraintInstanceDto(PredefinedConstraint.STUDENT_COLLISION.getName(),
            RewardPenalize.HARD_PENALIZE, params));
        instances.add(new ConstraintInstanceDto(PredefinedConstraint.ROOM_COLLISION.getName(),
            RewardPenalize.HARD_PENALIZE, params));
      }
    }

    int index = 0;
    List<TeacherProblemDto> teachers = timetable.teachers();
    for (SubjectProblemDto subject : timetable.subjects()) {
      for (StudentGroupProblemDto studentGroup : timetable.studentGroups()) {
        instances.add(
            new ConstraintInstanceDto(
                PredefinedConstraint.TEACHER_TEACHES_GROUP.getName(),
                RewardPenalize.HARD_REWARD,
                new ArrayList<>(List.of(
                    teachers.get(index++ % teachers.size()),
                    studentGroup,
                    subject
                ))));
      }
    }
  }
}
