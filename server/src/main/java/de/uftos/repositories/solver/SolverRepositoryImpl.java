package de.uftos.repositories.solver;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import ai.timefold.solver.core.impl.solver.DefaultSolverFactory;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.GradeProblemDto;
import de.uftos.dto.solver.LessonProblemDto;
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
import de.uftos.repositories.solver.timefold.constraints.ConstraintDefinitionFactory;
import de.uftos.repositories.solver.timefold.constraints.ConstraintDefinitionTimefoldInstance;
import de.uftos.repositories.solver.timefold.constraints.ConstraintInstanceFactory;
import de.uftos.repositories.solver.timefold.constraints.ConstraintInstanceTimefoldInstance;
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
import java.util.function.Consumer;
import org.springframework.stereotype.Repository;

/**
 * This class represents a implementation of the SolverRepository interface utilizing the Timefold solver.
 */
@Repository
public class SolverRepositoryImpl implements SolverRepository {

  @Override
  public Future<TimetableSolutionDto> solve(TimetableProblemDto timetable,
                                            Consumer<TimetableSolutionDto> solverFinishedEvent) {
    Callable<TimetableSolutionDto> solveTimetable = () -> {
      TimetableSolutionTimefoldInstance solution =
          getSolutionInstanceFromTimetableInstance(timetable);

      SolverConfig solverConfig = new SolverConfig()
          .withTerminationConfig(new TerminationConfig()
              .withUnimprovedMinutesSpentLimit(1L)
              .withBestScoreLimit("0hard/0soft"))
          .withSolutionClass(TimetableSolutionTimefoldInstance.class)
          .withEntityClassList(
              Arrays.stream(new Class<?>[] {LessonTimefoldInstance.class}).toList())
          .withScoreDirectorFactory(new ScoreDirectorFactoryConfig()
              .withEasyScoreCalculatorClass(ScoreCalculator.class));

      SolverFactory<TimetableSolutionTimefoldInstance> factory =
          new DefaultSolverFactory<>(solverConfig);

      Solver<TimetableSolutionTimefoldInstance> solver = factory.buildSolver();

      TimetableSolutionDto solutionDto =
          getTimetableInstanceFromSolutionInstance(solver.solve(solution));

      solverFinishedEvent.accept(solutionDto);

      return solutionDto;
    };

    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
    ExecutorService es = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue);

    return es.submit(solveTimetable);
  }

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  private TimetableSolutionTimefoldInstance getSolutionInstanceFromTimetableInstance(
      TimetableProblemDto timetable) {
    //checking timetable for valid input
    if (timetable.grades() == null
        || timetable.rooms() == null
        || timetable.subjects() == null
        || timetable.students() == null
        || timetable.studentGroups() == null
        || timetable.teachers() == null
        || timetable.timeslots() == null
        || timetable.lessons() == null
        || timetable.tags() == null
        || timetable.definitions() == null
        || timetable.instances() == null
        || timetable.teachers().isEmpty()   //elements are required to be able
        || timetable.rooms().isEmpty()      //to create lessons with non-null values
        || timetable.timeslots().isEmpty()) {
      throw new IllegalArgumentException();
    }


    //initializing all resources
    HashMap<String, GradeTimefoldInstance> grades = getGrades(timetable);
    HashMap<String, RoomTimefoldInstance> rooms = getRooms(timetable);
    HashMap<String, StudentGroupTimefoldInstance> studentGroups = getStudentGroups(timetable);
    HashMap<String, StudentTimefoldInstance> students = getStudents(timetable);
    HashMap<String, SubjectTimefoldInstance> subjects = getSubjects(timetable);
    HashMap<String, TeacherTimefoldInstance> teachers = getTeachers(timetable);
    HashMap<String, TimeslotTimefoldInstance> timeslots = getTimeslots(timetable);
    HashMap<String, TagTimefoldInstance> tags = getTags(timetable);
    HashMap<String, LessonTimefoldInstance> lessons = getLessons(timetable);

    TimetableSolutionTimefoldInstance solution = new TimetableSolutionTimefoldInstance();


    //defining and checking all relations between resources

    createLessonRoomRelation(lessons, rooms, timetable);
    createLessonSubjectRelation(lessons, subjects, timetable);
    createLessonStudentGroupRelation(lessons, studentGroups, timetable);
    createLessonTeacherRelation(lessons, teachers, timetable);
    createLessonTimeslotRelation(lessons, timeslots, timetable);

    createGradeTagRelation(grades, tags, timetable);
    createRoomTagRelation(rooms, tags, timetable);
    createStudentGroupTagRelation(studentGroups, tags, timetable);
    createStudentTagRelation(students, tags, timetable);
    createSubjectTagRelation(subjects, tags, timetable);
    createTeacherTagRelation(teachers, tags, timetable);
    createTimeslotTagRelation(timeslots, tags, timetable);

    createGradeStudentGroupRelation(grades, studentGroups, timetable);
    createStudentStudentGroupRelation(students, studentGroups, timetable);
    createSubjectTeacherRelation(subjects, teachers, timetable);

    //placing resources in solution instance

    solution.getGrades().addAll(grades.values());
    solution.getRooms().addAll(rooms.values());
    solution.getStudentGroups().addAll(studentGroups.values());
    solution.getStudents().addAll(students.values());
    solution.getSubjects().addAll(subjects.values());
    solution.getTeachers().addAll(teachers.values());
    solution.getTimeslots().addAll(timeslots.values());
    solution.getLessons().addAll(lessons.values());
    solution.getTags().addAll(tags.values());


    HashMap<String, ResourceTimefoldInstance> resources = new HashMap<>();

    resources.put("this", solution);
    resources.putAll(grades);
    resources.putAll(rooms);
    resources.putAll(studentGroups);
    resources.putAll(students);
    resources.putAll(subjects);
    resources.putAll(teachers);
    resources.putAll(timeslots);
    resources.putAll(lessons);
    resources.putAll(tags);

    //resources are done, continuing with constraints

    HashMap<String, ConstraintDefinitionTimefoldInstance> definitions = new HashMap<>();
    for (ConstraintDefinitionDto definition : timetable.definitions()) {
      definitions.put(definition.name(),
          ConstraintDefinitionFactory.getConstraintDefinition(definition));
    }

    List<ConstraintInstanceTimefoldInstance> constraintInstances = new ArrayList<>();

    for (ConstraintInstanceDto instance : timetable.instances()) {
      constraintInstances.add(
          ConstraintInstanceFactory.getConstraintInstance(instance, definitions, resources));
    }
    if (constraintInstances.isEmpty()) {
      //solver won't work without constraint instances
      ConstraintInstanceTimefoldInstance trivialInstance =
          new ConstraintInstanceTimefoldInstance(new ArrayList<>(), (list) -> false,
              RewardPenalize.SOFT_PENALIZE);
      constraintInstances.add(trivialInstance);
    }
    solution.getConstraintInstances().addAll(constraintInstances);

    return solution;
  }

  private TimetableSolutionDto getTimetableInstanceFromSolutionInstance(
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

    return new TimetableSolutionDto(lessons, solution.getScore().hardScore(),
        solution.getScore().softScore());

  }

  private HashMap<String, GradeTimefoldInstance> getGrades(TimetableProblemDto timetable) {
    HashMap<String, GradeTimefoldInstance> grades = new HashMap<>();

    for (GradeProblemDto grade : timetable.grades()) {
      grades.put(grade.id(), new GradeTimefoldInstance(grade.id()));
    }
    return grades;
  }

  private HashMap<String, RoomTimefoldInstance> getRooms(TimetableProblemDto timetable) {
    HashMap<String, RoomTimefoldInstance> rooms = new HashMap<>();

    for (RoomProblemDto room : timetable.rooms()) {
      rooms.put(room.id(), new RoomTimefoldInstance(room.id()));
    }
    return rooms;
  }

  private HashMap<String, StudentGroupTimefoldInstance> getStudentGroups(
      TimetableProblemDto timetable) {
    HashMap<String, StudentGroupTimefoldInstance> studentGroups = new HashMap<>();

    for (StudentGroupProblemDto studentGroup : timetable.studentGroups()) {
      studentGroups.put(studentGroup.id(), new StudentGroupTimefoldInstance(studentGroup.id()));
    }
    return studentGroups;
  }

  private HashMap<String, StudentTimefoldInstance> getStudents(TimetableProblemDto timetable) {
    HashMap<String, StudentTimefoldInstance> students = new HashMap<>();

    for (StudentProblemDto student : timetable.students()) {
      students.put(student.id(), new StudentTimefoldInstance(student.id()));
    }
    return students;
  }

  private HashMap<String, SubjectTimefoldInstance> getSubjects(TimetableProblemDto timetable) {
    HashMap<String, SubjectTimefoldInstance> subjects = new HashMap<>();

    for (SubjectProblemDto subject : timetable.subjects()) {
      subjects.put(subject.id(), new SubjectTimefoldInstance(subject.id()));
    }
    return subjects;
  }

  private HashMap<String, TeacherTimefoldInstance> getTeachers(TimetableProblemDto timetable) {
    HashMap<String, TeacherTimefoldInstance> teachers = new HashMap<>();

    for (TeacherProblemDto teacher : timetable.teachers()) {
      teachers.put(teacher.id(), new TeacherTimefoldInstance(teacher.id()));
    }
    return teachers;
  }

  private HashMap<String, TimeslotTimefoldInstance> getTimeslots(TimetableProblemDto timetable) {
    HashMap<String, TimeslotTimefoldInstance> timeslots = new HashMap<>();

    for (TimeslotProblemDto timeslot : timetable.timeslots()) {
      TimeslotTimefoldInstance timefoldInstance = new TimeslotTimefoldInstance(timeslot.id());
      timefoldInstance.setDayOfWeek(timeslot.day());
      timefoldInstance.setSlotId(timeslot.slot());

      timeslots.put(timeslot.id(), timefoldInstance);
    }
    return timeslots;
  }

  private HashMap<String, TagTimefoldInstance> getTags(TimetableProblemDto timetable) {
    HashMap<String, TagTimefoldInstance> tags = new HashMap<>();

    for (TagProblemDto tag : timetable.tags()) {
      tags.put(tag.id(), new TagTimefoldInstance(tag.id()));
    }
    return tags;
  }

  private HashMap<String, LessonTimefoldInstance> getLessons(TimetableProblemDto timetable) {
    HashMap<String, LessonTimefoldInstance> lessons = new HashMap<>();

    for (LessonProblemDto lesson : timetable.lessons()) {
      LessonTimefoldInstance timefoldInstance = new LessonTimefoldInstance(lesson.id());
      timefoldInstance.setIndex(lesson.index());

      lessons.put(lesson.id(), timefoldInstance);
    }
    return lessons;
  }

  private void createLessonStudentGroupRelation(
      HashMap<String, LessonTimefoldInstance> lessons,
      HashMap<String, StudentGroupTimefoldInstance> studentGroups,
      TimetableProblemDto timetable) {

    for (StudentGroupProblemDto studentGroup : timetable.studentGroups()) {
      StudentGroupTimefoldInstance studentGroupTimefoldInstance =
          studentGroups.get(studentGroup.id());
      if (studentGroupTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String lessonId : studentGroup.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson == null || lesson.getStudentGroup() != null) {
          throw new IllegalArgumentException();
        }

        lesson.setStudentGroup(studentGroupTimefoldInstance);
        studentGroupTimefoldInstance.getLessonList().add(lesson);
      }
    }

    for (LessonProblemDto lesson : timetable.lessons()) {
      LessonTimefoldInstance lessonTimefoldInstance = lessons.get(lesson.id());
      if (lessonTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (lessonTimefoldInstance.getStudentGroup() == null
          || !lessonTimefoldInstance.getStudentGroup().getId().equals(lesson.studentGroupId())) {
        throw new IllegalArgumentException();
      }
    }
  }

  private void createLessonSubjectRelation(
      HashMap<String, LessonTimefoldInstance> lessons,
      HashMap<String, SubjectTimefoldInstance> subjects,
      TimetableProblemDto timetable) {

    for (SubjectProblemDto subject : timetable.subjects()) {
      SubjectTimefoldInstance subjectTimefoldInstance = subjects.get(subject.id());
      if (subjectTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String lessonId : subject.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson == null || lesson.getSubject() != null) {
          throw new IllegalArgumentException();
        }

        lesson.setSubject(subjectTimefoldInstance);
        subjectTimefoldInstance.getLessonList().add(lesson);
      }
    }

    for (LessonProblemDto lesson : timetable.lessons()) {
      LessonTimefoldInstance lessonTimefoldInstance = lessons.get(lesson.id());
      if (lessonTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (lessonTimefoldInstance.getSubject() == null
          || !lessonTimefoldInstance.getSubject().getId().equals(lesson.subjectId())) {
        throw new IllegalArgumentException();
      }
    }
  }

  private void createLessonTimeslotRelation(
      HashMap<String, LessonTimefoldInstance> lessons,
      HashMap<String, TimeslotTimefoldInstance> timeslots,
      TimetableProblemDto timetable) {

    for (TimeslotProblemDto timeslot : timetable.timeslots()) {
      TimeslotTimefoldInstance timeslotTimefoldInstance = timeslots.get(timeslot.id());
      if (timeslotTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String lessonId : timeslot.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson == null || lesson.getTimeslot() != null) {
          throw new IllegalArgumentException();
        }

        lesson.setTimeslot(timeslotTimefoldInstance);
        timeslotTimefoldInstance.getLessonList().add(lesson);
      }
    }

    for (LessonProblemDto lesson : timetable.lessons()) {
      LessonTimefoldInstance lessonTimefoldInstance = lessons.get(lesson.id());
      if (lessonTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (lessonTimefoldInstance.getTimeslot() == null) {
        if (lesson.timeslotId() != null) {
          throw new IllegalArgumentException();
        }

        //setting timeslot to prevent null values
        TimeslotTimefoldInstance timeslot = timeslots.values().iterator().next();
        timeslot.getLessonList().add(lessonTimefoldInstance);
        lessonTimefoldInstance.setTimeslot(timeslot);

      } else if (!lessonTimefoldInstance.getTimeslot().getId().equals(lesson.timeslotId())) {
        throw new IllegalArgumentException();
      }
    }
  }

  private void createLessonTeacherRelation(
      HashMap<String, LessonTimefoldInstance> lessons,
      HashMap<String, TeacherTimefoldInstance> teachers,
      TimetableProblemDto timetable) {

    for (TeacherProblemDto teacher : timetable.teachers()) {
      TeacherTimefoldInstance teacherTimefoldInstance = teachers.get(teacher.id());
      if (teacherTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String lessonId : teacher.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson == null || lesson.getTeacher() != null) {
          throw new IllegalArgumentException();
        }

        lesson.setTeacher(teacherTimefoldInstance);
        teacherTimefoldInstance.getLessonList().add(lesson);
      }
    }

    for (LessonProblemDto lesson : timetable.lessons()) {
      LessonTimefoldInstance lessonTimefoldInstance = lessons.get(lesson.id());
      if (lessonTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (lessonTimefoldInstance.getTeacher() == null) {
        if (lesson.teacherId() != null) {
          throw new IllegalArgumentException();
        }

        //setting teacher to prevent null values
        TeacherTimefoldInstance teacher = teachers.values().iterator().next();
        teacher.getLessonList().add(lessonTimefoldInstance);
        lessonTimefoldInstance.setTeacher(teacher);

      } else if (!lessonTimefoldInstance.getTeacher().getId().equals(lesson.teacherId())) {
        throw new IllegalArgumentException();
      }
    }
  }

  private void createLessonRoomRelation(
      HashMap<String, LessonTimefoldInstance> lessons,
      HashMap<String, RoomTimefoldInstance> rooms,
      TimetableProblemDto timetable) {

    for (RoomProblemDto room : timetable.rooms()) {
      RoomTimefoldInstance roomTimefoldInstance = rooms.get(room.id());
      if (roomTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String lessonId : room.lessonIds()) {
        LessonTimefoldInstance lesson = lessons.get(lessonId);
        if (lesson == null || lesson.getRoom() != null) {
          throw new IllegalArgumentException();
        }

        lesson.setRoom(roomTimefoldInstance);
        roomTimefoldInstance.getLessonList().add(lesson);
      }
    }

    for (LessonProblemDto lesson : timetable.lessons()) {
      LessonTimefoldInstance lessonTimefoldInstance = lessons.get(lesson.id());
      if (lessonTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (lessonTimefoldInstance.getRoom() == null) {
        if (lesson.roomId() != null) {
          throw new IllegalArgumentException();
        }

        //setting room to prevent null values
        RoomTimefoldInstance room = rooms.values().iterator().next();
        room.getLessonList().add(lessonTimefoldInstance);
        lessonTimefoldInstance.setRoom(room);

      } else if (!lessonTimefoldInstance.getRoom().getId().equals(lesson.roomId())) {
        throw new IllegalArgumentException();
      }
    }
  }

  private void createGradeTagRelation(
      HashMap<String, GradeTimefoldInstance> grades,
      HashMap<String, TagTimefoldInstance> tags,
      TimetableProblemDto timetable) {

    for (GradeProblemDto grade : timetable.grades()) {
      GradeTimefoldInstance gradeTimefoldInstance = grades.get(grade.id());
      if (gradeTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String tagId : grade.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        if (tag == null) {
          throw new IllegalArgumentException();
        }

        tag.getGradeList().add(gradeTimefoldInstance);
        gradeTimefoldInstance.getProvidedTagsList().add(tag);
      }
    }

    for (TagProblemDto tag : timetable.tags()) {
      TagTimefoldInstance tagTimefoldInstance = tags.get(tag.id());
      if (tagTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (tag.gradeIds().size() != tagTimefoldInstance.getGradeList().size()) {
        throw new IllegalArgumentException();
      }
      for (GradeTimefoldInstance grade : tagTimefoldInstance.getGradeList()) {
        if (!tag.gradeIds().contains(grade.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  private void createRoomTagRelation(
      HashMap<String, RoomTimefoldInstance> rooms,
      HashMap<String, TagTimefoldInstance> tags,
      TimetableProblemDto timetable) {

    for (RoomProblemDto room : timetable.rooms()) {
      RoomTimefoldInstance roomTimefoldInstance = rooms.get(room.id());
      if (roomTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String tagId : room.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        if (tag == null) {
          throw new IllegalArgumentException();
        }

        tag.getRoomList().add(roomTimefoldInstance);
        roomTimefoldInstance.getProvidedTagsList().add(tag);
      }
    }

    for (TagProblemDto tag : timetable.tags()) {
      TagTimefoldInstance tagTimefoldInstance = tags.get(tag.id());
      if (tagTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (tag.roomIds().size() != tagTimefoldInstance.getRoomList().size()) {
        throw new IllegalArgumentException();
      }
      for (RoomTimefoldInstance room : tagTimefoldInstance.getRoomList()) {
        if (!tag.roomIds().contains(room.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  private void createStudentGroupTagRelation(
      HashMap<String, StudentGroupTimefoldInstance> studentGroups,
      HashMap<String, TagTimefoldInstance> tags,
      TimetableProblemDto timetable) {

    for (StudentGroupProblemDto studentGroup : timetable.studentGroups()) {
      StudentGroupTimefoldInstance studentGroupTimefoldInstance =
          studentGroups.get(studentGroup.id());
      if (studentGroupTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String tagId : studentGroup.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        if (tag == null) {
          throw new IllegalArgumentException();
        }

        tag.getStudentGroupList().add(studentGroupTimefoldInstance);
        studentGroupTimefoldInstance.getProvidedTagsList().add(tag);
      }
    }

    for (TagProblemDto tag : timetable.tags()) {
      TagTimefoldInstance tagTimefoldInstance = tags.get(tag.id());
      if (tagTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (tag.studentGroupIds().size() != tagTimefoldInstance.getStudentGroupList().size()) {
        throw new IllegalArgumentException();
      }
      for (StudentGroupTimefoldInstance studentGroup : tagTimefoldInstance.getStudentGroupList()) {
        if (!tag.studentGroupIds().contains(studentGroup.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  private void createStudentTagRelation(
      HashMap<String, StudentTimefoldInstance> students,
      HashMap<String, TagTimefoldInstance> tags,
      TimetableProblemDto timetable) {

    for (StudentProblemDto student : timetable.students()) {
      StudentTimefoldInstance studentTimefoldInstance = students.get(student.id());
      if (studentTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String tagId : student.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        if (tag == null) {
          throw new IllegalArgumentException();
        }

        tag.getStudentList().add(studentTimefoldInstance);
        studentTimefoldInstance.getProvidedTagsList().add(tag);
      }
    }

    for (TagProblemDto tag : timetable.tags()) {
      TagTimefoldInstance tagTimefoldInstance = tags.get(tag.id());
      if (tagTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (tag.studentIds().size() != tagTimefoldInstance.getStudentList().size()) {
        throw new IllegalArgumentException();
      }
      for (StudentTimefoldInstance student : tagTimefoldInstance.getStudentList()) {
        if (!tag.studentIds().contains(student.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  private void createSubjectTagRelation(
      HashMap<String, SubjectTimefoldInstance> subjects,
      HashMap<String, TagTimefoldInstance> tags,
      TimetableProblemDto timetable) {

    for (SubjectProblemDto subject : timetable.subjects()) {
      SubjectTimefoldInstance subjectTimefoldInstance = subjects.get(subject.id());
      if (subjectTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String tagId : subject.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        if (tag == null) {
          throw new IllegalArgumentException();
        }

        tag.getSubjectList().add(subjectTimefoldInstance);
        subjectTimefoldInstance.getProvidedTagsList().add(tag);
      }
    }

    for (TagProblemDto tag : timetable.tags()) {
      TagTimefoldInstance tagTimefoldInstance = tags.get(tag.id());
      if (tagTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (tag.subjectIds().size() != tagTimefoldInstance.getSubjectList().size()) {
        throw new IllegalArgumentException();
      }
      for (SubjectTimefoldInstance subject : tagTimefoldInstance.getSubjectList()) {
        if (!tag.subjectIds().contains(subject.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  private void createTeacherTagRelation(
      HashMap<String, TeacherTimefoldInstance> teachers,
      HashMap<String, TagTimefoldInstance> tags,
      TimetableProblemDto timetable) {

    for (TeacherProblemDto teacher : timetable.teachers()) {
      TeacherTimefoldInstance teacherTimefoldInstance = teachers.get(teacher.id());
      if (teacherTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String tagId : teacher.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        if (tag == null) {
          throw new IllegalArgumentException();
        }

        tag.getTeacherList().add(teacherTimefoldInstance);
        teacherTimefoldInstance.getProvidedTagsList().add(tag);
      }
    }

    for (TagProblemDto tag : timetable.tags()) {
      TagTimefoldInstance tagTimefoldInstance = tags.get(tag.id());
      if (tagTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (tag.teacherIds().size() != tagTimefoldInstance.getTeacherList().size()) {
        throw new IllegalArgumentException();
      }
      for (TeacherTimefoldInstance teacher : tagTimefoldInstance.getTeacherList()) {
        if (!tag.teacherIds().contains(teacher.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  private void createTimeslotTagRelation(
      HashMap<String, TimeslotTimefoldInstance> timeslots,
      HashMap<String, TagTimefoldInstance> tags,
      TimetableProblemDto timetable) {

    for (TimeslotProblemDto timeslot : timetable.timeslots()) {
      TimeslotTimefoldInstance timeslotTimefoldInstance = timeslots.get(timeslot.id());
      if (timeslotTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String tagId : timeslot.tagIds()) {
        TagTimefoldInstance tag = tags.get(tagId);
        if (tag == null) {
          throw new IllegalArgumentException();
        }

        tag.getTimeslotList().add(timeslotTimefoldInstance);
        timeslotTimefoldInstance.getProvidedTagsList().add(tag);
      }
    }

    for (TagProblemDto tag : timetable.tags()) {
      TagTimefoldInstance tagTimefoldInstance = tags.get(tag.id());
      if (tagTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (tag.timeslotIds().size() != tagTimefoldInstance.getTimeslotList().size()) {
        throw new IllegalArgumentException();
      }
      for (TimeslotTimefoldInstance timeslot : tagTimefoldInstance.getTimeslotList()) {
        if (!tag.timeslotIds().contains(timeslot.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  private void createGradeStudentGroupRelation(
      HashMap<String, GradeTimefoldInstance> grades,
      HashMap<String, StudentGroupTimefoldInstance> studentGroups,
      TimetableProblemDto timetable) {

    for (GradeProblemDto grade : timetable.grades()) {
      GradeTimefoldInstance gradeTimefoldInstance =
          grades.get(grade.id());
      if (gradeTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String studentGroupId : grade.studentGroupIds()) {
        StudentGroupTimefoldInstance studentGroup = studentGroups.get(studentGroupId);
        if (studentGroup == null || studentGroup.getGrade() != null) {
          throw new IllegalArgumentException();
        }

        studentGroup.setGrade(gradeTimefoldInstance);
        gradeTimefoldInstance.getStudentGroupList().add(studentGroup);
      }
    }

    for (StudentGroupProblemDto studentGroup : timetable.studentGroups()) {
      StudentGroupTimefoldInstance studentGroupTimefoldInstance =
          studentGroups.get(studentGroup.id());
      if (studentGroupTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (studentGroupTimefoldInstance.getGrade() == null
          || !studentGroupTimefoldInstance.getGrade().getId().equals(studentGroup.gradeId())) {
        throw new IllegalArgumentException();
      }
    }
  }

  private void createStudentStudentGroupRelation(
      HashMap<String, StudentTimefoldInstance> students,
      HashMap<String, StudentGroupTimefoldInstance> studentGroups,
      TimetableProblemDto timetable) {

    for (StudentProblemDto student : timetable.students()) {
      StudentTimefoldInstance studentTimefoldInstance = students.get(student.id());
      if (studentTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String studentGroupId : student.studentGroupIds()) {
        StudentGroupTimefoldInstance studentGroup = studentGroups.get(studentGroupId);
        if (studentGroup == null) {
          throw new IllegalArgumentException();
        }

        studentGroup.getStudentList().add(studentTimefoldInstance);
        studentTimefoldInstance.getStudentGroupList().add(studentGroup);
      }
    }

    for (StudentGroupProblemDto studentGroup : timetable.studentGroups()) {
      StudentGroupTimefoldInstance studentGroupTimefoldInstance =
          studentGroups.get(studentGroup.id());
      if (studentGroupTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (studentGroup.studentIds().size()
          != studentGroupTimefoldInstance.getStudentList().size()) {
        throw new IllegalArgumentException();
      }
      for (StudentTimefoldInstance student : studentGroupTimefoldInstance.getStudentList()) {
        if (!studentGroup.studentIds().contains(student.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }

  private void createSubjectTeacherRelation(
      HashMap<String, SubjectTimefoldInstance> subjects,
      HashMap<String, TeacherTimefoldInstance> teachers,
      TimetableProblemDto timetable) {

    for (SubjectProblemDto subject : timetable.subjects()) {
      SubjectTimefoldInstance subjectTimefoldInstance = subjects.get(subject.id());
      if (subjectTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      for (String teacherId : subject.teacherIds()) {
        TeacherTimefoldInstance teacher = teachers.get(teacherId);
        if (teacher == null) {
          throw new IllegalArgumentException();
        }

        teacher.getSubjectList().add(subjectTimefoldInstance);
        subjectTimefoldInstance.getTeacherList().add(teacher);
      }
    }

    for (TeacherProblemDto teacher : timetable.teachers()) {
      TeacherTimefoldInstance teacherTimefoldInstance = teachers.get(teacher.id());
      if (teacherTimefoldInstance == null) {
        throw new IllegalStateException();
      }
      if (teacher.subjectIds().size() != teacherTimefoldInstance.getSubjectList().size()) {
        throw new IllegalArgumentException();
      }
      for (SubjectTimefoldInstance subject : teacherTimefoldInstance.getSubjectList()) {
        if (!teacher.subjectIds().contains(subject.getId())) {
          throw new IllegalArgumentException();
        }
      }
    }
  }
}
