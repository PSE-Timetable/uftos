package de.uftos.solver;

import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.Timetable;
import de.uftos.repositories.solver.SolverRepository;
import de.uftos.timefold.domain.GradeTimefoldInstance;
import de.uftos.timefold.domain.LessonTimefoldInstance;
import de.uftos.timefold.domain.RoomTimefoldInstance;
import de.uftos.timefold.domain.StudentGroupTimefoldInstance;
import de.uftos.timefold.domain.StudentTimefoldInstance;
import de.uftos.timefold.domain.SubjectTimefoldInstance;
import de.uftos.timefold.domain.TagTimefoldInstance;
import de.uftos.timefold.domain.TeacherTimefoldInstance;
import de.uftos.timefold.domain.TimeslotTimefoldInstance;
import de.uftos.timefold.domain.TimetableSolutionTimefoldInstance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

//todo: fix timetable translation

public class TimefoldSolver implements SolverRepository {

  /*
  public Future<Timetable> solve(HashMap<PredefinedConstraints, PredefinedConstraint> definitions,
                                 List<PredefinedConstraintInstance> instances,
                                 Timetable timetable) {
    Callable<Timetable> solveTimetable = () -> {
      TimetableSolutionTimefoldInstance solution =
          getSolutionInstanceFromTimetableInstance(timetable);

      for (PredefinedConstraintInstance i : instances) {
        List<ResourceTimefoldInstance> parameters = new ArrayList<>();
        for (Resource resource : i.parameters()) {
          switch (resource.getResourceType()) {
            case ROOM -> {
              parameters.add(solution.getRooms().get(resource.getId()));
            }
            case STUDENT -> {
              parameters.add(solution.getStudents().get(resource.getId()));
            }
            case TEACHER -> {
              parameters.add(solution.getTeachers().get(resource.getId()));
            }
            case TAG -> {
              parameters.add(solution.getTags().get(resource.getId()));
            }
            case GRADE -> {
              parameters.add(solution.getGrades().get(resource.getId()));
            }
            case LESSON -> {
              parameters.add(solution.getLessons().get(resource.getId()));
            }
            case SUBJECT -> {
              parameters.add(solution.getSubjects().get(resource.getId()));
            }
            case TIMESLOT -> {
              parameters.add(solution.getTimeslots().get(resource.getId()));
            }
            case TIMETABLE -> {
              parameters.add(solution);
            }
            case STUDENT_GROUP -> {
              parameters.add(solution.getStudentGroups().get(resource.getId()));
            }
          }
        }
        //solution.getConstrains().add(
        //  new de.uftos.timefold.constraints.PredefinedConstraintInstance(i.name(), parameters));
      }

      SolverConfig solverConfig = new SolverConfig()
          .withTerminationConfig(new TerminationConfig()
              .withMillisecondsSpentLimit(1800000L))
          .withSolutionClass(TimetableSolutionTimefoldInstance.class)
          .withEntityClassList(
              Arrays.stream(new Class<?>[] {LessonTimefoldInstance.class}).toList())
          .withScoreDirectorFactory(
              new ScoreDirectorFactoryConfig().withEasyScoreCalculatorClass(ScoreCalculator.class));

      SolverFactory<TimetableSolutionTimefoldInstance> factory =
          new DefaultSolverFactory<>(solverConfig);

      Solver<TimetableSolutionTimefoldInstance> solver = factory.buildSolver();

      System.out.println(solver);
      System.out.println(solver.getClass());

      return getTimetableInstanceFromSolutionInstance(solver.solve(solution));
    };

    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
    ExecutorService es = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue);

    return es.submit(solveTimetable);
  }

  public Future<Timetable> solve(Timetable timetable,
                                 HashMap<String, ConstraintDefinitionDto> definitions,
                                 List<ConstraintInstanceDto> instances) {

    Callable<Timetable> solveTimetable = () -> {
      TimetableSolutionTimefoldInstance solution =
          getSolutionInstanceFromTimetableInstance(timetable);

      SolverConfig solverConfig = new SolverConfig()
          .withTerminationConfig(new TerminationConfig()
              .withMillisecondsSpentLimit(5000L))
          .withSolutionClass(TimetableSolutionTimefoldInstance.class)
          .withEntityClassList(
              Arrays.stream(new Class<?>[] {LessonTimefoldInstance.class}).toList());
      solverConfig.setScoreDirectorFactoryConfig(
          new ScoreDirectorFactoryConfig().withConstraintProviderClass(
              ConstraintProviderTimefoldInstance.class));

      SolverFactory<TimetableSolutionTimefoldInstance> factory =
          new DefaultSolverFactory<>(solverConfig);

      Solver<TimetableSolutionTimefoldInstance> solver = factory.buildSolver();

      solver.solve(solution);

      return getTimetableInstanceFromSolutionInstance(solution);
    };

    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
    ExecutorService es = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue);

    return es.submit(solveTimetable);
  }
   */

  TimetableSolutionTimefoldInstance getSolutionInstanceFromTimetableInstance(Timetable timetable) {
    List<GradeTimefoldInstance> grades = new ArrayList<>();
    List<RoomTimefoldInstance> rooms = new ArrayList<>();
    List<StudentGroupTimefoldInstance> studentGroups = new ArrayList<>();
    List<StudentTimefoldInstance> students = new ArrayList<>();
    List<SubjectTimefoldInstance> subjects = new ArrayList<>();
    List<TeacherTimefoldInstance> teachers = new ArrayList<>();
    List<TimeslotTimefoldInstance> timeslots = new ArrayList<>();
    List<TagTimefoldInstance> tags = new ArrayList<>();
    List<LessonTimefoldInstance> lessons = new ArrayList<>();

    /*
    for (Grade g : timetable.grades()) {
      grades.add(new GradeTimefoldInstance(g.id()));
    }

    for (Room r : timetable.rooms()) {
      rooms.add(new RoomTimefoldInstance(r.id()));
    }

    for (StudentGroup sg : timetable.studentGroups()) {
      StudentGroupTimefoldInstance studentGroup =
          new StudentGroupTimefoldInstance(sg.id(), grades.get(sg.gradeId()));
      grades.get(sg.gradeId()).getStudentGroupList().add(studentGroup);
      studentGroups.add(studentGroup);
    }

    for (Student s : timetable.students()) {
      StudentTimefoldInstance student = new StudentTimefoldInstance(s.id());
      for (int i : s.studentGroupIdList()) {
        student.getStudentGroupList().add(studentGroups.get(i));
        studentGroups.get(i).getStudentList().add(student);
      }
      students.add(student);
    }

    for (Subject s : timetable.subjects()) {
      subjects.add(new SubjectTimefoldInstance(s.id()));
    }

    for (Teacher t : timetable.teachers()) {
      TeacherTimefoldInstance teacher = new TeacherTimefoldInstance(t.id());
      for (int i : t.subjectIdList()) {
        teacher.getSubjectList().add(subjects.get(i));
        subjects.get(i).getTeacherList().add(teacher);
      }
      teachers.add(teacher);
    }

    for (Timeslot t : timetable.timeslots()) {
      timeslots.add(new TimeslotTimefoldInstance(t.id(), t.dayOfWeek(), t.slot()));
    }

    for (Lesson l : timetable.lessons()) {
      lessons.add(new LessonTimefoldInstance(l.id(), l.index(), subjects.get(l.subjectId()),
          studentGroups.get(l.studentGroupId())));
    }

    for (Tag t : timetable.tags()) {
      TagTimefoldInstance tag = new TagTimefoldInstance(t.id());
      for (Student s : timetable.students()) {
        if (s.providedTagsIdList().contains(t.id())) {
          tag.getStudentList().add(students.get(s.id()));
        }
      }
      for (StudentGroup sg : timetable.studentGroups()) {
        if (sg.providedTagsIdList().contains(t.id())) {
          tag.getStudentGroupList().add(studentGroups.get(sg.id()));
        }
      }
      for (Teacher teacher : timetable.teachers()) {
        if (teacher.providedTagsIdList().contains(t.id())) {
          tag.getTeacherList().add(teachers.get(teacher.id()));
        }
      }
      for (Room r : timetable.rooms()) {
        if (r.providedTagsIdList().contains(t.id())) {
          tag.getRoomList().add(rooms.get(r.id()));
        }
      }
      for (Subject s : timetable.subjects()) {
        if (s.providedTagsIdList().contains(t.id())) {
          tag.getSubjectList().add(subjects.get(s.id()));
        }
      }
      for (Grade g : timetable.grades()) {
        if (g.providedTagsIdList().contains(t.id())) {
          tag.getGradeList().add(grades.get(g.id()));
        }
      }
      for (Timeslot timeslot : timetable.timeslots()) {
        if (timeslot.providedTagsIdList().contains(t.id())) {
          tag.getTimeslotList().add(timeslots.get(timeslot.id()));
        }
      }
      tags.add(new TagTimefoldInstance(t.id()));
    }
     */

    return new TimetableSolutionTimefoldInstance(grades, rooms, studentGroups, students, subjects,
        teachers, timeslots, tags, lessons);
  }

  Timetable getTimetableInstanceFromSolutionInstance(TimetableSolutionTimefoldInstance solution) {
    System.out.println(solution.getScore());
    List<Grade> grades = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    List<StudentGroup> studentGroups = new ArrayList<>();
    List<Student> students = new ArrayList<>();
    List<Subject> subjects = new ArrayList<>();
    List<Teacher> teachers = new ArrayList<>();
    List<Timeslot> timeslots = new ArrayList<>();
    List<Tag> tags = new ArrayList<>();
    List<Lesson> lessons = new ArrayList<>();

    /*
    for (GradeTimefoldInstance g : solution.getGrades()) {
      List<Integer> tagList = new ArrayList<>();
      for (TagTimefoldInstance t : g.getProvidedTagsList()) {
        tagList.add(t.getId());
      }
      List<Integer> studentGroupList = new ArrayList<>();
      for (StudentGroupTimefoldInstance sg : g.getStudentGroupList()) {
        studentGroupList.add(sg.getId());
      }
      grades.add(new Grade(g.getId(), tagList, studentGroupList));
    }

    for (RoomTimefoldInstance r : solution.getRooms()) {
      List<Integer> tagList = new ArrayList<>();
      for (TagTimefoldInstance t : r.getProvidedTagsList()) {
        tagList.add(t.getId());
      }
      List<Integer> lessonList = new ArrayList<>();
      for (LessonTimefoldInstance l : solution.getLessons().stream()
          .filter((lesson) -> (lesson.getRoom() != null && lesson.getRoom().getId() == r.getId()))
          .toList()) {
        lessonList.add(l.getId());
      }
      rooms.add(new Room(r.getId(), tagList, lessonList));
    }

    for (StudentGroupTimefoldInstance sg : solution.getStudentGroups()) {
      List<Integer> tagList = new ArrayList<>();
      for (TagTimefoldInstance t : sg.getProvidedTagsList()) {
        tagList.add(t.getId());
      }
      List<Integer> lessonList = new ArrayList<>();
      for (LessonTimefoldInstance l : solution.getLessons().stream().filter(
          (lesson) -> (lesson.getStudentGroup() != null &&
              lesson.getStudentGroup().getId() == sg.getId())).toList()) {
        lessonList.add(l.getId());
      }
      List<Integer> studentList = new ArrayList<>();
      for (StudentTimefoldInstance s : sg.getStudentList()) {
        studentList.add(s.getId());
      }
      if (sg.getGrade() == null) {
        studentGroups.add(new StudentGroup(sg.getId(), -1, tagList, studentList, lessonList));
      } else {
        studentGroups.add(
            new StudentGroup(sg.getId(), sg.getGrade().getId(), tagList, studentList, lessonList));
      }
    }

    for (StudentTimefoldInstance s : solution.getStudents()) {
      List<Integer> tagList = new ArrayList<>();
      for (TagTimefoldInstance t : s.getProvidedTagsList()) {
        tagList.add(t.getId());
      }
      List<Integer> studentGroupList = new ArrayList<>();
      for (StudentGroupTimefoldInstance sg : s.getStudentGroupList()) {
        studentGroupList.add(sg.getId());
      }
      students.add(new Student(s.getId(), tagList, studentGroupList));
    }

    for (SubjectTimefoldInstance s : solution.getSubjects()) {
      List<Integer> tagList = new ArrayList<>();
      for (TagTimefoldInstance t : s.getProvidedTagsList()) {
        tagList.add(t.getId());
      }
      List<Integer> lessonList = new ArrayList<>();
      for (LessonTimefoldInstance l : solution.getLessons().stream().filter(
              (lesson) -> (lesson.getSubject() != null && lesson.getSubject().getId() == s.getId()))
          .toList()) {
        lessonList.add(l.getId());
      }
      List<Integer> teacherList = new ArrayList<>();
      for (TeacherTimefoldInstance t : s.getTeacherList()) {
        teacherList.add(t.getId());
      }
      subjects.add(new Subject(s.getId(), tagList, lessonList, teacherList));
    }

    for (TeacherTimefoldInstance t : solution.getTeachers()) {
      List<Integer> tagList = new ArrayList<>();
      for (TagTimefoldInstance tag : t.getProvidedTagsList()) {
        tagList.add(tag.getId());
      }
      List<Integer> subjectList = new ArrayList<>();
      for (SubjectTimefoldInstance s : t.getSubjectList()) {
        subjectList.add(s.getId());
      }
      List<Integer> lessonList = new ArrayList<>();
      for (LessonTimefoldInstance l : solution.getLessons().stream().filter(
              (lesson) -> (lesson.getTeacher() != null && lesson.getTeacher().getId() == t.getId()))
          .toList()) {
        lessonList.add(l.getId());
      }
      teachers.add(new Teacher(t.getId(), tagList, subjectList, lessonList));
    }

    for (TimeslotTimefoldInstance t : solution.getTimeslots()) {
      List<Integer> tagList = new ArrayList<>();
      for (TagTimefoldInstance tag : t.getProvidedTagsList()) {
        tagList.add(tag.getId());
      }
      List<Integer> lessonList = new ArrayList<>();
      for (LessonTimefoldInstance l : solution.getLessons().stream().filter(
              (lesson) -> (lesson.getTimeslot() != null && lesson.getTimeslot().getId() == t.getId()))
          .toList()) {
        lessonList.add(l.getId());
      }
      timeslots.add(new Timeslot(t.getId(), t.getDayOfWeek(), t.getSlotId(), tagList, lessonList));
    }

    for (TagTimefoldInstance t : solution.getTags()) {
      List<Integer> studentList = new ArrayList<>();
      for (StudentTimefoldInstance s : t.getStudentList()) {
        studentList.add(s.getId());
      }
      List<Integer> studentGroupList = new ArrayList<>();
      for (StudentGroupTimefoldInstance sg : t.getStudentGroupList()) {
        studentGroupList.add(sg.getId());
      }
      List<Integer> teacherList = new ArrayList<>();
      for (TeacherTimefoldInstance teacher : t.getTeacherList()) {
        teacherList.add(teacher.getId());
      }
      List<Integer> roomList = new ArrayList<>();
      for (RoomTimefoldInstance r : t.getRoomList()) {
        roomList.add(r.getId());
      }
      List<Integer> subjectList = new ArrayList<>();
      for (SubjectTimefoldInstance s : t.getSubjectList()) {
        subjectList.add(s.getId());
      }
      List<Integer> gradeList = new ArrayList<>();
      for (GradeTimefoldInstance g : t.getGradeList()) {
        gradeList.add(g.getId());
      }
      List<Integer> timeslotList = new ArrayList<>();
      for (TimeslotTimefoldInstance slot : t.getTimeslotList()) {
        timeslotList.add(slot.getId());
      }
      tags.add(new Tag(t.getId(), studentList, studentGroupList, teacherList, roomList, subjectList,
          gradeList, timeslotList));
    }

    for (LessonTimefoldInstance l : solution.getLessons()) {
      int timeslotId = -1;
      if (l.getTimeslot() != null) {
        timeslotId = l.getTimeslot().getId();
      }
      int teacherId = -1;
      if (l.getTeacher() != null) {
        teacherId = l.getTeacher().getId();
      }
      int studentGroupId = -1;
      if (l.getStudentGroup() != null) {
        studentGroupId = l.getStudentGroup().getId();
      }
      int subjectId = -1;
      if (l.getSubject() != null) {
        subjectId = l.getSubject().getId();
      }
      int roomId = -1;
      if (l.getRoom() != null) {
        roomId = l.getRoom().getId();
      }
      lessons.add(
          new Lesson(l.getId(), l.getIndex(), timeslotId, teacherId, studentGroupId, subjectId,
              roomId));
    }

    return new Timetable(grades, rooms, studentGroups, students, subjects, teachers, timeslots,
        lessons, tags);

     */
    return null;

  }

  @Override
  public Future<TimetableSolutionDto> solve(TimetableProblemDto timetable,
                                            HashMap<String, ConstraintDefinitionDto> constaintDefinitions,
                                            List<ConstraintInstanceDto> constraintInstances)
      throws IllegalArgumentException {
    return null;
  }
}
