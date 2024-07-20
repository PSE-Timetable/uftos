package de.uftos.solver;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import ai.timefold.solver.core.impl.solver.DefaultSolverFactory;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.LessonProblemDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.solver.TimetableSolutionDto;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.repositories.solver.SolverRepository;
import de.uftos.solver.timefold.domain.GradeTimefoldInstance;
import de.uftos.solver.timefold.domain.LessonTimefoldInstance;
import de.uftos.solver.timefold.domain.RoomTimefoldInstance;
import de.uftos.solver.timefold.domain.StudentGroupTimefoldInstance;
import de.uftos.solver.timefold.domain.StudentTimefoldInstance;
import de.uftos.solver.timefold.domain.SubjectTimefoldInstance;
import de.uftos.solver.timefold.domain.TagTimefoldInstance;
import de.uftos.solver.timefold.domain.TeacherTimefoldInstance;
import de.uftos.solver.timefold.domain.TimeslotTimefoldInstance;
import de.uftos.solver.timefold.domain.TimetableSolutionTimefoldInstance;
import de.uftos.solver.timefold.solver.ConstraintProviderTimefoldInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimefoldSolver implements SolverRepository {

  TimetableSolutionTimefoldInstance getSolutionInstanceFromTimetableInstance(
      TimetableProblemDto timetable) {
    //todo: build entire  problem instance from TimetableProblemDto
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

  TimetableSolutionDto getTimetableInstanceFromSolutionInstance(
      TimetableSolutionTimefoldInstance solution) {
    List<LessonProblemDto> lessons = new ArrayList<>();

    for (LessonTimefoldInstance lesson : solution.getLessons()) {
      lessons.add(
          new LessonProblemDto(
              lesson.getId(),
              lesson.getIndex(),
              lesson.getTeacher().getId(),
              lesson.getStudentGroup().getId(),
              lesson.getTimeslot().getId(),
              lesson.getSubject().getId(),
              lesson.getRoom().getId())
      );
    }

    return new TimetableSolutionDto(lessons);

  }

  @Override
  public Future<TimetableSolutionDto> solve(TimetableProblemDto timetable,
                                            HashMap<String, ConstraintDefinitionDto> constaintDefinitions,
                                            List<ConstraintInstanceDto> constraintInstances)
      throws IllegalArgumentException {
    Callable<TimetableSolutionDto> solveTimetable = () -> {
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
}
