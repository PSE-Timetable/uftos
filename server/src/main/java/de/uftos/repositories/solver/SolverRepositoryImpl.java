package de.uftos.repositories.solver;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import ai.timefold.solver.core.config.constructionheuristic.ConstructionHeuristicType;
import ai.timefold.solver.core.config.localsearch.LocalSearchPhaseConfig;
import ai.timefold.solver.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import ai.timefold.solver.core.impl.solver.DefaultSolverFactory;
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
import de.uftos.repositories.solver.timefold.constraints.ConstraintDefinitionFactory;
import de.uftos.repositories.solver.timefold.constraints.ConstraintDefinitionTimefoldInstance;
import de.uftos.repositories.solver.timefold.constraints.ConstraintInstanceFactory;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.GradeTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.LessonTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.RoomTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentGroupTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.SubjectTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TagTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TeacherTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimeslotTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimetableSolutionTimefoldInstance;
import de.uftos.repositories.solver.timefold.solver.ScoreCalculator;
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
public class SolverRepositoryImpl implements SolverRepository {

  TimetableSolutionTimefoldInstance getSolutionInstanceFromTimetableInstance(
      TimetableProblemDto timetable) {
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
      definitions.put(definition.name(),
          ConstraintDefinitionFactory.getConstraintDefinition(definition));
    }

    //initializing all resources
    for (ResourceProblemDto resource : timetable.getResources()) {
      switch (resource.getType()) {
        case GRADE -> grades.put(resource.getId(), new GradeTimefoldInstance(resource.getId()));
        case LESSON -> lessons.put(resource.getId(), new LessonTimefoldInstance(resource.getId()));
        case ROOM -> rooms.put(resource.getId(), new RoomTimefoldInstance(resource.getId()));
        case STUDENT_GROUP ->
            studentGroups.put(resource.getId(), new StudentGroupTimefoldInstance(resource.getId()));
        case STUDENT ->
            students.put(resource.getId(), new StudentTimefoldInstance(resource.getId()));
        case SUBJECT ->
            subjects.put(resource.getId(), new SubjectTimefoldInstance(resource.getId()));
        case TAG -> tags.put(resource.getId(), new TagTimefoldInstance(resource.getId()));
        case TEACHER ->
            teachers.put(resource.getId(), new TeacherTimefoldInstance(resource.getId()));
        case TIMESLOT ->
            timeslots.put(resource.getId(), new TimeslotTimefoldInstance(resource.getId()));
        default -> throw new IllegalStateException();
      }
    }

    //connecting all resources as defined by the Dtos (and checking connection)

    for (GradeProblemDto grade : timetable.grades()) {
      GradeTimefoldInstance timefoldInstance = grades.get(grade.id());
      for (String studentGroupId : grade.studentGroupIds()) {
        StudentGroupTimefoldInstance studentGroup = studentGroups.get(studentGroupId);
        if (studentGroup.getGrade() != null) {
          throw new IllegalArgumentException();
        }
        studentGroup.setGrade(timefoldInstance);
        timefoldInstance.getStudentGroupList().add(studentGroup);
      }
      for (String tagId : grade.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        tag.getGradeList().add(timefoldInstance);
        timefoldInstance.getProvidedTagsList().add(tag);
      }
    }
    solution.getGrades().addAll(grades.values());
    resources.putAll(grades);

    for (RoomProblemDto room : timetable.rooms()) {
      RoomTimefoldInstance timefoldInstance = rooms.get(room.id());
      for (String lessonId : room.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        lesson.setRoom(timefoldInstance);
        timefoldInstance.getLessonList().add(lesson);
      }
      for (String tagId : room.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        tag.getRoomList().add(timefoldInstance);
        timefoldInstance.getProvidedTagsList().add(tag);
      }
    }
    solution.getRooms().addAll(rooms.values());
    resources.putAll(rooms);

    for (StudentGroupProblemDto studentGroup : timetable.studentGroups()) {
      StudentGroupTimefoldInstance timefoldInstance = studentGroups.get(studentGroup.id());
      for (String lessonId : studentGroup.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson.getStudentGroup() != null) {
          throw new IllegalArgumentException();
        }
        lesson.setStudentGroup(timefoldInstance);
        timefoldInstance.getLessonList().add(lesson);
      }
      for (String studentId : studentGroup.studentIds()) {
        StudentTimefoldInstance student = students.get(studentId);
        student.getStudentGroupList().add(timefoldInstance);
        timefoldInstance.getStudentList().add(student);
      }
      for (String tagId : studentGroup.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        tag.getStudentGroupList().add(timefoldInstance);
        timefoldInstance.getProvidedTagsList().add(tag);
      }
      if (timefoldInstance.getGrade() != grades.get(studentGroup.gradeId())
          || !grades.get(studentGroup.gradeId()).getStudentGroupList().contains(timefoldInstance)) {
        throw new IllegalStateException();
      }
    }
    solution.getStudentGroups().addAll(studentGroups.values());
    resources.putAll(studentGroups);

    for (StudentProblemDto student : timetable.students()) {
      StudentTimefoldInstance timefoldInstance = students.get(student.id());
      if (student.studentGroupIds().size() != timefoldInstance.getStudentGroupList().size()) {
        throw new IllegalArgumentException();
      }
      for (String studentGroupId : student.studentGroupIds()) {
        StudentGroupTimefoldInstance studentGroup = studentGroups.get(studentGroupId);
        if (!timefoldInstance.getStudentGroupList().contains(studentGroup)
            || !studentGroup.getStudentList().contains(timefoldInstance)) {
          throw new IllegalStateException();
        }
      }
      for (String tagId : student.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        tag.getStudentList().add(timefoldInstance);
        timefoldInstance.getProvidedTagsList().add(tag);
      }
    }
    solution.getStudents().addAll(students.values());
    resources.putAll(students);

    for (SubjectProblemDto subject : timetable.subjects()) {
      SubjectTimefoldInstance timefoldInstance = subjects.get(subject.id());
      for (String lessonId : subject.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson.getSubject() != null) {
          throw new IllegalArgumentException();
        }
        lesson.setSubject(timefoldInstance);
        timefoldInstance.getLessonList().add(lesson);
      }
      for (String teacherId : subject.teacherIds()) {
        TeacherTimefoldInstance teacher = teachers.get(teacherId);
        teacher.getSubjectList().add(timefoldInstance);
        timefoldInstance.getTeacherList().add(teacher);
      }
      for (String tagId : subject.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        tag.getSubjectList().add(timefoldInstance);
        timefoldInstance.getProvidedTagsList().add(tag);
      }
    }
    solution.getSubjects().addAll(subjects.values());
    resources.putAll(subjects);

    for (TeacherProblemDto teacher : timetable.teachers()) {
      TeacherTimefoldInstance timefoldInstance = teachers.get(teacher.id());
      if (teacher.subjectIds().size() != timefoldInstance.getSubjectList().size()) {
        throw new IllegalArgumentException();
      }
      for (String lessonId : teacher.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson.getTeacher() != null) {
          throw new IllegalArgumentException();
        }
        lesson.setTeacher(timefoldInstance);
        timefoldInstance.getLessonList().add(lesson);
      }
      for (String subjectId : teacher.subjectIds()) {
        SubjectTimefoldInstance subject = subjects.get(subjectId);
        if (!timefoldInstance.getSubjectList().contains(subject)
            || !subject.getTeacherList().contains(timefoldInstance)) {
          throw new IllegalStateException();
        }
      }
      for (String tagId : teacher.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        tag.getTeacherList().add(timefoldInstance);
        timefoldInstance.getProvidedTagsList().add(tag);
      }
    }
    solution.getTeachers().addAll(teachers.values());
    resources.putAll(teachers);

    for (TimeslotProblemDto timeslot : timetable.timeslots()) {
      TimeslotTimefoldInstance timefoldInstance = timeslots.get(timeslot.id());
      timefoldInstance.setDayOfWeek(timeslot.day());
      timefoldInstance.setSlotId(timeslot.slot());

      for (String lessonId : timeslot.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson.getTimeslot() != null) {
          throw new IllegalArgumentException();
        }
        lesson.setTimeslot(timefoldInstance);
        timefoldInstance.getLessonList().add(lesson);
      }
      for (String tagId : timeslot.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        tag.getTimeslotList().add(timefoldInstance);
        timefoldInstance.getProvidedTagsList().add(tag);
      }
    }
    solution.getTimeslots().addAll(timeslots.values());
    resources.putAll(timeslots);

    for (LessonProblemDto lesson : timetable.lessons()) {
      LessonTimefoldInstance timefoldInstance = lessons.get(lesson.id());
      timefoldInstance.setIndex(lesson.index());

      //checking subject for consistency
      if (timefoldInstance.getSubject() == null
          || lesson.subjectId() == null
          || subjects.get(lesson.subjectId()) != timefoldInstance.getSubject()) {
        throw new IllegalArgumentException();
      }
      if (!timefoldInstance.getSubject().getLessonList().contains(timefoldInstance)) {
        throw new IllegalStateException();
      }

      //checking student group for consistency
      if (timefoldInstance.getStudentGroup() == null
          || lesson.studentGroupId() == null
          || studentGroups.get(lesson.studentGroupId()) != timefoldInstance.getStudentGroup()) {
        throw new IllegalArgumentException();
      }
      if (!timefoldInstance.getStudentGroup().getLessonList().contains(timefoldInstance)) {
        throw new IllegalStateException();
      }

      //checking teacher for consistency
      if (!(timefoldInstance.getTeacher() == null && lesson.teacherId() == null)) {
        if (lesson.teacherId() == null
            || teachers.get(lesson.teacherId()) != timefoldInstance.getTeacher()) {
          throw new IllegalArgumentException();
        }
        if (!teachers.get(lesson.teacherId()).getLessonList().contains(timefoldInstance)) {
          throw new IllegalStateException();
        }
      } else {
        TeacherTimefoldInstance teacher = teachers.values().stream().toList().getFirst();
        timefoldInstance.setTeacher(teacher);
        teacher.getLessonList().add(timefoldInstance);
      }

      //checking timeslot for consistency
      if (!(timefoldInstance.getTimeslot() == null && lesson.timeslotId() == null)) {
        if (lesson.timeslotId() == null
            || timeslots.get(lesson.timeslotId()) != timefoldInstance.getTimeslot()) {
          throw new IllegalArgumentException();
        }
        if (!timeslots.get(lesson.timeslotId()).getLessonList().contains(timefoldInstance)) {
          throw new IllegalStateException();
        }
      } else {
        TimeslotTimefoldInstance timeslot = timeslots.values().stream().toList().getFirst();
        timefoldInstance.setTimeslot(timeslot);
        timeslot.getLessonList().add(timefoldInstance);
      }

      //checking room for consistency
      if (!(timefoldInstance.getRoom() == null && lesson.roomId() == null)) {
        if (lesson.roomId() == null
            || rooms.get(lesson.roomId()) != timefoldInstance.getRoom()) {
          throw new IllegalArgumentException();
        }
        if (!rooms.get(lesson.roomId()).getLessonList().contains(timefoldInstance)) {
          throw new IllegalStateException();
        }
      } else {
        RoomTimefoldInstance room = rooms.values().stream().toList().getFirst();
        timefoldInstance.setRoom(room);
        room.getLessonList().add(timefoldInstance);
      }
    }
    solution.getLessons().addAll(lessons.values());
    resources.putAll(lessons);

    for (TagProblemDto tag : timetable.tags()) {
      TagTimefoldInstance timefoldInstance = tags.get(tag.getId());

      //checking grades for consistency
      if (tag.gradeIds().size() != timefoldInstance.getGradeList().size()) {
        throw new IllegalArgumentException();
      }
      for (String gradeId : tag.gradeIds()) {
        GradeTimefoldInstance grade = grades.get(gradeId);
        if (!grade.getProvidedTagsList().contains(timefoldInstance)
            || !timefoldInstance.getGradeList().contains(grade)) {
          throw new IllegalStateException();
        }
      }

      //checking rooms for consistency
      if (tag.roomIds().size() != timefoldInstance.getRoomList().size()) {
        throw new IllegalArgumentException();
      }
      for (String roomId : tag.roomIds()) {
        RoomTimefoldInstance room = rooms.get(roomId);
        if (!room.getProvidedTagsList().contains(timefoldInstance)
            || !timefoldInstance.getRoomList().contains(room)) {
          throw new IllegalStateException();
        }
      }

      //checking students for consistency
      if (tag.studentIds().size() != timefoldInstance.getStudentList().size()) {
        throw new IllegalArgumentException();
      }
      for (String studentId : tag.studentIds()) {
        StudentTimefoldInstance student = students.get(studentId);
        if (!student.getProvidedTagsList().contains(timefoldInstance)
            || !timefoldInstance.getStudentList().contains(student)) {
          throw new IllegalStateException();
        }
      }

      //checking student groups for consistency
      if (tag.studentGroupIds().size() != timefoldInstance.getStudentGroupList().size()) {
        throw new IllegalArgumentException();
      }
      for (String studentGroupId : tag.studentGroupIds()) {
        StudentGroupTimefoldInstance studentGroup = studentGroups.get(studentGroupId);
        if (!studentGroup.getProvidedTagsList().contains(timefoldInstance)
            || !timefoldInstance.getStudentGroupList().contains(studentGroup)) {
          throw new IllegalStateException();
        }
      }

      //checking subjects for consistency
      if (tag.subjectIds().size() != timefoldInstance.getSubjectList().size()) {
        throw new IllegalArgumentException();
      }
      for (String subjectId : tag.subjectIds()) {
        SubjectTimefoldInstance subject = subjects.get(subjectId);
        if (!subject.getProvidedTagsList().contains(timefoldInstance)
            || !timefoldInstance.getSubjectList().contains(subject)) {
          throw new IllegalStateException();
        }
      }

      //checking teachers for consistency
      if (tag.teacherIds().size() != timefoldInstance.getTeacherList().size()) {
        throw new IllegalArgumentException();
      }
      for (String teacherId : tag.teacherIds()) {
        TeacherTimefoldInstance teacher = teachers.get(teacherId);
        if (!teacher.getProvidedTagsList().contains(timefoldInstance)
            || !timefoldInstance.getTeacherList().contains(teacher)) {
          throw new IllegalStateException();
        }
      }

      //checking timeslots for consistency
      if (tag.timeslotIds().size() != timefoldInstance.getTimeslotList().size()) {
        throw new IllegalArgumentException();
      }
      for (String timeslotId : tag.timeslotIds()) {
        TimeslotTimefoldInstance timeslot = timeslots.get(timeslotId);
        if (!timeslot.getProvidedTagsList().contains(timefoldInstance)
            || !timefoldInstance.getTimeslotList().contains(timeslot)) {
          throw new IllegalStateException();
        }
      }
    }
    solution.getTags().addAll(tags.values());
    resources.putAll(tags);

    for (ConstraintInstanceDto instance : timetable.instances()) {
      constraintInstances.add(
          ConstraintInstanceFactory.getConstraintInstance(instance, definitions, resources));
    }
    solution.getConstraintInstances().addAll(constraintInstances);

    return solution;
  }

  TimetableSolutionDto getTimetableInstanceFromSolutionInstance(
      TimetableSolutionTimefoldInstance solution) {
    List<LessonProblemDto> lessons = new ArrayList<>();

    for (LessonTimefoldInstance lesson : solution.getLessons()) {
      String teacherId = null;
      if (lesson.getTeacher() != null) {
        teacherId = lesson.getTeacher().getId();
      }
      String timeslotId = null;
      if (lesson.getTimeslot() != null) {
        timeslotId = lesson.getTimeslot().getId();
      }
      String roomId = null;
      if (lesson.getRoom() != null) {
        roomId = lesson.getRoom().getId();
      }
      lessons.add(
          new LessonProblemDto(
              lesson.getId(),
              lesson.getIndex(),
              teacherId,
              lesson.getStudentGroup().getId(),
              timeslotId,
              lesson.getSubject().getId(),
              roomId
          )
      );
    }

    return new TimetableSolutionDto(lessons);

  }

  @Override
  public Future<TimetableSolutionDto> solve(TimetableProblemDto timetable)
      throws IllegalArgumentException {
    Callable<TimetableSolutionDto> solveTimetable = () -> {
      TimetableSolutionTimefoldInstance solution =
          getSolutionInstanceFromTimetableInstance(timetable);


      ConstructionHeuristicPhaseConfig constructionHeuristic =
          new ConstructionHeuristicPhaseConfig();
      constructionHeuristic.setConstructionHeuristicType(
          ConstructionHeuristicType.FIRST_FIT);

      LocalSearchPhaseConfig localSearch = new LocalSearchPhaseConfig();
      localSearch.setAcceptorConfig(
          new LocalSearchAcceptorConfig().withSimulatedAnnealingStartingTemperature(
              "5hard/100soft"));


      SolverConfig solverConfig = new SolverConfig()
          .withTerminationConfig(new TerminationConfig()
              .withMillisecondsSpentLimit(60000L))
          .withSolutionClass(TimetableSolutionTimefoldInstance.class)
          .withEntityClassList(
              Arrays.stream(new Class<?>[] {LessonTimefoldInstance.class}).toList())
          .withScoreDirectorFactory(new ScoreDirectorFactoryConfig()
              .withEasyScoreCalculatorClass(ScoreCalculator.class));


      SolverFactory<TimetableSolutionTimefoldInstance> factory =
          new DefaultSolverFactory<>(solverConfig);

      Solver<TimetableSolutionTimefoldInstance> solver = factory.buildSolver();

      TimetableSolutionTimefoldInstance solved = solver.solve(solution);

      System.out.println(solved.getScore());

      return getTimetableInstanceFromSolutionInstance(solved);
    };

    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
    ExecutorService es = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue);

    return es.submit(solveTimetable);
  }
}
