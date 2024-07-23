package de.uftos;

import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.GradeProblemDto;
import de.uftos.dto.solver.LessonProblemDto;
import de.uftos.dto.solver.RoomProblemDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.entities.Grade;
import de.uftos.entities.Room;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
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
                  null));
        }
      }
    }

    List<RoomProblemDto> rooms = new ArrayList<>();
    for (Room room : roomRepository.findAll()) {
      List<String> lessonIds = new ArrayList<>();
      for (LessonProblemDto lesson : lessons) {
        if (lesson.roomId().equals(room.getId())) {
          lessonIds.add(lesson.id());
        }
      }
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : room.getTags()) {
        tagIds.add(tag.getId());
      }
      rooms.add(new RoomProblemDto(room.getId(), tagIds, lessonIds));
    }

    //TODO:
    /*
    List<StudentGroupProblemDto> studentGroups = new ArrayList<>();
    for (StudentGroup studentGroup : studentGroupRepository.findAll()) {
      List<String> lessonIds = new ArrayList<>();
      for (LessonProblemDto lesson : lessons) {
        if (lesson.roomId().equals(room.getId())) {
          lessonIds.add(lesson.id());
        }
      }
      List<String> tagIds = new ArrayList<>();
      for (Tag tag : room.getTags()) {
        tagIds.add(tag.getId());
      }
      studentGroups.add(new StudentGroupProblemDto(studentGroup.getId(), studentGroup.getGrades().getFirst().getId(), ));
    }
     */

    //todo: create missing lists
    TimetableProblemDto timetable =
        new TimetableProblemDto(
            grades,
            lessons,
            rooms,
            null,
            null,
            null,
            null,
            null,
            null,
            getDefinitions(),
            getInstances()
        );


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

  //TODO
  private List<ConstraintDefinitionDto> getDefinitions() {
    return new ArrayList<>();
  }

  //TODO
  private List<ConstraintInstanceDto> getInstances() {
    return new ArrayList<>();
  }

}
