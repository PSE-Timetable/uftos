package de.uftos.demoDataSource;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DemoDataSource {
  //todo: fix resource generation
  public static List<PredefinedConstraintInstance> getPredefinedConstraintInstances(
      TimetableProblemDto timetable,
      HashMap<PredefinedConstraints, PredefinedConstraint> constraints) {
    List<PredefinedConstraintInstance> instances = new ArrayList<>();
        /*
        if (constraints.containsKey(PredefinedConstraints.STUDENT_CONFLICT)) {
            System.out.println("Students: " + timetable.students().size());
            for (Student s : timetable.students()) {
                List<Resource> parameters = new ArrayList<>();
                parameters.add(s);
                instances.add(new PredefinedConstraintInstance(PredefinedConstraints.STUDENT_CONFLICT, parameters));
            }
        }
        if (constraints.containsKey(PredefinedConstraints.TEACHER_CONFLICT)) {
            System.out.println("Teachers: " + timetable.teachers().size());
            for (Teacher t : timetable.teachers()) {
                List<Resource> parameters = new ArrayList<>();
                parameters.add(t);
                instances.add(new PredefinedConstraintInstance(PredefinedConstraints.TEACHER_CONFLICT, parameters));
            }
        }
        if (constraints.containsKey(PredefinedConstraints.ROOM_CONFLICT)) {
            System.out.println("Rooms: " + timetable.rooms().size());
            for (Room r : timetable.rooms()) {
                List<Resource> parameters = new ArrayList<>();
                parameters.add(r);
                instances.add(new PredefinedConstraintInstance(PredefinedConstraints.ROOM_CONFLICT, parameters));
            }
        }
        if (constraints.containsKey(PredefinedConstraints.LESSON_VALIDATION)) {
            System.out.println("Lessons: " + timetable.lessons().size());
            for (Lesson l : timetable.lessons()) {
                List<Resource> parameters = new ArrayList<>();
                parameters.add(l);
                instances.add(new PredefinedConstraintInstance(PredefinedConstraints.LESSON_VALIDATION, parameters));
            }
        }

         */
    return instances;
  }

  public static HashMap<PredefinedConstraints, PredefinedConstraint> getPredefinedConstraintDefinitions() {
    HashMap<PredefinedConstraints, PredefinedConstraint> constraints = new HashMap<>();
    constraints.put(PredefinedConstraints.ROOM_CONFLICT, new RoomConflict());
    constraints.put(PredefinedConstraints.STUDENT_CONFLICT, new StudentConflict());
    constraints.put(PredefinedConstraints.TEACHER_CONFLICT, new TeacherConflict());
    constraints.put(PredefinedConstraints.LESSON_VALIDATION, new LessonValidation());
    return constraints;
  }

  private static List<GradeProblemDto> getGrades(int size) {
    List<GradeProblemDto> grades = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      //grades.add(new Grade(i, new ArrayList<>(), new ArrayList<>()));
    }

    return grades;
  }

  private static List<RoomProblemDto> getRooms(int size) {
    List<RoomProblemDto> rooms = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      //rooms.add(new Room(i, new ArrayList<>(), new ArrayList<>()));
    }

    return rooms;
  }

  private static List<StudentGroupProblemDto> getStudentGroups(int size, int grades) {
    List<StudentGroupProblemDto> studentGroups = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      //studentGroups.add(new StudentGroup(i, (int)(Math.random() * grades), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    return studentGroups;
  }

  private static List<StudentProblemDto> getStudents(int size) {
    List<StudentProblemDto> students = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      //students.add(new Student(i, new ArrayList<>(), new ArrayList<>()));
    }

    return students;
  }

  private static List<SubjectProblemDto> getSubjects(int size) {
    List<SubjectProblemDto> subjects = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      //subjects.add(new Subject(i, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    return subjects;
  }

  private static List<TeacherProblemDto> getTeachers(int size) {
    List<TeacherProblemDto> teachers = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      //teachers.add(new Teacher(i, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    return teachers;
  }

  private static List<TimeslotProblemDto> getTimeslots(int size) {
    List<TimeslotProblemDto> timeslots = new ArrayList<>();
    int id = 0;

    for (int day = 0; day < 7; day++) {
      for (int slot = 0; slot < size; slot++) {
        //timeslots.add(new Timeslot(id++, day, slot, new ArrayList<>(), new ArrayList<>()));
      }
    }

    return timeslots;
  }

  private static List<LessonProblemDto> getLessons(int[][] curriculum,
                                                   List<StudentGroupProblemDto> studentGroups) {
    List<LessonProblemDto> lessons = new ArrayList<>();
        /*
        int id = 0;
        for (StudentGroup sg : studentGroups) {
            int[] subjects = curriculum[sg.gradeId()];
            for (int subject = 0; subject < subjects.length; subject++) {
                for (int index = 0; index < subjects[subject]; index++) {
                    lessons.add(new Lesson(id++, index, -1, -1, sg.getId(), subject, -1));
                }
            }
        }

         */
    return lessons;
  }

  private static List<TagProblemDto> getTags(int size) {
    List<TagProblemDto> tags = new ArrayList<>();

    for (int i = 0; i < size; i++) {
      //tags.add(new Tag(i, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    return tags;
  }

  public static TimetableProblemDto getDemoTimetable() {
    return getDemoTimetable(5, 10, 15, 2, 2, 18, 10, 2);
  }

  public static TimetableProblemDto getDemoTimetable(int grades, int rooms, int studentGroups,
                                                     int students,
                                                     int subjects, int teachers,
                                                     int timeslotsPerDay,
                                                     int tags) {
    List<GradeProblemDto> gradeList = getGrades(grades);
    List<RoomProblemDto> roomList = getRooms(rooms);
    List<StudentGroupProblemDto> studentGroupList = getStudentGroups(studentGroups, grades);
        /*
        for (StudentGroup sg : studentGroupList) {
            gradeList.get(sg.gradeId()).studentGroupIdList().add(sg.id());
        }
        */
    List<StudentProblemDto> studentList = getStudents(students);
        /*
        for (Student s : studentList) {
            int studentGroup = (int) (Math.random() * studentGroups);
            s.studentGroupIdList().add(studentGroup);
            studentGroupList.get(studentGroup).studentIdList().add(s.id());
        }
        */
    List<SubjectProblemDto> subjectList = getSubjects(subjects);
    List<TeacherProblemDto> teacherList = getTeachers(teachers);
        /*
        for (Teacher t : teacherList) {
            for (int i = 0; i < 3 && i < subjects; i++) {
                int subject = 0;
                do {
                    subject = (int)(Math.random() * subjects);
                } while (t.subjectIdList().contains(subject));
                t.subjectIdList().add(subject);
                subjectList.get(subject).teacherIdList().add(t.id());
            }
        }
        */
    List<TimeslotProblemDto> timeslotList = getTimeslots(timeslotsPerDay);

    int[][] curriculum = new int[grades][subjects];
    for (int grade = 0; grade < grades; grade++) {
      for (int subject = 0; subject < subjects; subject++) {
        curriculum[grade][subject] = (int) (Math.random() * 4);
      }
    }
    List<LessonProblemDto> lessonList = getLessons(curriculum, studentGroupList);
        /*
        for (Lesson l : lessonList) {
            if (l.studentGroupId() >= 0) {
                studentGroupList.get(l.studentGroupId()).lessonIdList().add(l.id());
            }
            if (l.teacherId() >= 0) {
                teacherList.get(l.teacherId()).lessonIdList().add(l.id());
            }
            if (l.subjectId() >= 0) {
                subjectList.get(l.subjectId()).lessonIdList().add(l.id());
            }
            if (l.timeslotId() >= 0) {
                timeslotList.get(l.timeslotId()).lessonIdList().add(l.id());
            }
            if (l.roomId() >= 0) {
                roomList.get(l.roomId()).lessonIdList().add(l.id());
            }
        }
        */
    List<TagProblemDto> tagList = getTags(tags);

    return new TimetableProblemDto(gradeList, lessonList, roomList, studentGroupList, studentList,
        subjectList, tagList, teacherList, timeslotList);

  }
}
