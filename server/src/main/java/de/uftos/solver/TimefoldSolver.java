package de.uftos.solver;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import ai.timefold.solver.core.impl.solver.DefaultSolverFactory;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.GradeProblemDto;
import de.uftos.dto.solver.LessonProblemDto;
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
import de.uftos.repositories.solver.SolverRepository;
import de.uftos.solver.timefold.constraints.ConstraintDefinitionFactory;
import de.uftos.solver.timefold.constraints.ConstraintDefinitionTimefoldInstance;
import de.uftos.solver.timefold.constraints.ConstraintInstanceFactory;
import de.uftos.solver.timefold.constraints.constraintinstances.ConstraintInstanceTimefoldInstance;
import de.uftos.solver.timefold.domain.GradeTimefoldInstance;
import de.uftos.solver.timefold.domain.LessonTimefoldInstance;
import de.uftos.solver.timefold.domain.ResourceTimefoldInstance;
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

/**
 * This class represents a implementation of the SolverRepository interface utilizing the Timefold solver.
 */
public class TimefoldSolver implements SolverRepository {

  TimetableSolutionTimefoldInstance getSolutionInstanceFromTimetableInstance(
      TimetableProblemDto timetable) {
    //todo: build entire  problem instance from TimetableProblemDto
    HashMap<String, GradeTimefoldInstance> grades = new HashMap<>();
    HashMap<String, RoomTimefoldInstance> rooms = new HashMap<>();
    HashMap<String, StudentGroupTimefoldInstance> studentGroups = new HashMap<>();
    HashMap<String, StudentTimefoldInstance> students = new HashMap<>();
    HashMap<String, SubjectTimefoldInstance> subjects = new HashMap<>();
    HashMap<String, TeacherTimefoldInstance> teachers = new HashMap<>();
    HashMap<String, TimeslotTimefoldInstance> timeslots = new HashMap<>();
    HashMap<String, TagTimefoldInstance> tags = new HashMap<>();
    HashMap<String, LessonTimefoldInstance> lessons = new HashMap<>();
    List<ConstraintInstanceTimefoldInstance> constraintInstances = new ArrayList<>();

    TimetableSolutionTimefoldInstance solution = new TimetableSolutionTimefoldInstance();
    HashMap<String, ResourceTimefoldInstance> resources = new HashMap<>();
    resources.put("this", solution);

    HashMap<String, ConstraintDefinitionTimefoldInstance> definitions = new HashMap<>();

    for (ConstraintDefinitionDto definition : timetable.definitions()) {
      definitions.put(definition.name(), ConstraintDefinitionFactory.getConstraintDefinition(definition));
    }


    for (GradeProblemDto grade : timetable.grades()) {
      grades.put(grade.id(), null);
    }
    solution.getGrades().addAll(grades.values());
    resources.putAll(grades);

    for (RoomProblemDto room : timetable.rooms()) {
      rooms.put(room.id(), null);
    }
    solution.getRooms().addAll(rooms.values());
    resources.putAll(rooms);

    for (StudentGroupProblemDto studentGroup : timetable.studentGroups()) {
      studentGroups.put(studentGroup.id(), null);
    }
    solution.getStudentGroups().addAll(studentGroups.values());
    resources.putAll(studentGroups);

    for (StudentProblemDto student : timetable.students()) {
      students.put(student.id(), null);
    }
    solution.getStudents().addAll(students.values());
    resources.putAll(students);

    for (SubjectProblemDto subject : timetable.subjects()) {
      subjects.put(subject.id(), null);
    }
    solution.getSubjects().addAll(subjects.values());
    resources.putAll(subjects);

    for (TeacherProblemDto teacher : timetable.teachers()) {
      teachers.put(teacher.id(), null);
    }
    solution.getTeachers().addAll(teachers.values());
    resources.putAll(teachers);

    for (TimeslotProblemDto timeslot : timetable.timeslots()) {
      timeslots.put(timeslot.id(), null);
    }
    solution.getTimeslots().addAll(timeslots.values());
    resources.putAll(timeslots);

    for (LessonProblemDto lesson : timetable.lessons()) {
      lessons.put(lesson.id(), null);
    }
    solution.getLessons().addAll(lessons.values());
    resources.putAll(lessons);

    for (TagProblemDto tag : timetable.tags()) {
      tags.put(tag.id(), null);
    }
    solution.getTags().addAll(tags.values());
    resources.putAll(tags);

    for (ConstraintInstanceDto instance : timetable.instances()) {
      constraintInstances.add(ConstraintInstanceFactory.getConstraintInstance(instance, definitions, resources));
    }
    solution.getConstraintInstances().addAll(constraintInstances);

    return solution;
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
