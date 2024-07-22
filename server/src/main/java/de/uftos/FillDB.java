package de.uftos;

import de.uftos.dto.Weekday;
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
import java.util.ArrayList;
import java.util.List;

public class FillDB {

  public static void fillDB(
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
    System.out.println("filling DB");

    createGrades(gradeRepository, 4);
    System.out.println("created grades");
    createSubjects(subjectRepository, 4);
    System.out.println("created subjects");
    createRooms(roomRepository, 6, 2, 30, 5);
    System.out.println("created rooms");
    createStudents(studentRepository, 10);
    System.out.println("created students");
    createStudentGroups(studentGroupRepository, 5);
    System.out.println("created student groups");
    createTeachers(teacherRepository, 4);
    System.out.println("created teachers");
    createTags(tagRepository, 2);
    System.out.println("created tags");
    createTimeslots(timeslotRepository, 6);
    System.out.println("created timeslots");

    /*
    for (Grade grade : gradeRepository.findAll()) {
      List<LessonsCountRequestDto> lessonsCounts = new ArrayList<>();
      for (Subject subject : subjectRepository.findAll()) {
        lessonsCounts.add(new LessonsCountRequestDto(subject.getId(), 3));
      }
      curriculumRepository.save(new Curriculum(grade.getId(), lessonsCounts));
    }
    System.out.println("created curriculum");
     */

    List<Grade> grades = gradeRepository.findAll();
    for (StudentGroup studentGroup : studentGroupRepository.findAll()) {
      int index = (int) (Math.random() * grades.size());
      grades.get(index).getStudentGroups().add(studentGroup);
    }
    gradeRepository.saveAll(grades);
    System.out.println("created grade - student group mapping");

    List<StudentGroup> groups = studentGroupRepository.findAll();
    for (Student student : studentRepository.findAll()) {
      groups.get((int) (Math.random() * groups.size())).getStudents().add(student);
    }
    studentGroupRepository.saveAll(groups);
    System.out.println("created student - student group mapping");

    for (Teacher teacher : teacherRepository.findAll()) {
      teacher.getSubjects().addAll(subjectRepository.findAll());
      teacherRepository.save(teacher);
    }
    System.out.println("created teacher - subject mapping");

    System.out.println("DB filled");
  }

  public static void createGrades(GradeRepository repo, int amount) {
    for (int index = 0; index < amount; index++) {
      repo.save(new Grade("Grade-" + index, new ArrayList<>(), new ArrayList<>()));
    }
  }

  public static void createSubjects(SubjectRepository repo, int amount) {
    for (int index = 0; index < amount; index++) {
      repo.save(new Subject("Subject-" + index, "", new ArrayList<>()));
    }
  }

  public static void createRooms(RoomRepository repo, int amount, int buildings, int baseCapacity,
                                 int capacityVariety) {
    for (int index = 0; index < amount; index++) {
      repo.save(new Room("Room-" + index, "" + (int) (Math.random() * buildings),
          baseCapacity + (int) (Math.random() * capacityVariety), new ArrayList<>()));
    }
  }

  public static void createStudentGroups(StudentGroupRepository repo, int amount) {
    for (int index = 0; index < amount; index++) {
      repo.save(new StudentGroup("Group-" + index, new ArrayList<>(), new ArrayList<>(),
          new ArrayList<>()));
    }
  }

  public static void createStudents(StudentRepository repo, int amount) {
    for (int index = 0; index < amount; index++) {
      repo.save(new Student("Student-" + index, "Lastname-" + index, new ArrayList<>()));
    }
  }

  public static void createTeachers(TeacherRepository repo, int amount) {
    for (int index = 0; index < amount; index++) {
      repo.save(new Teacher("Teacher-" + index, "Lastname-" + index, "" + index, new ArrayList<>(),
          new ArrayList<>()));
    }
  }

  public static void createTags(TagRepository repo, int amount) {
    for (int index = 0; index < amount; index++) {
      repo.save(new Tag(null, "Tag-" + index));
    }
  }

  public static void createTimeslots(TimeslotRepository repo, int amount) {
    List<Weekday> weekdays = new ArrayList<>(5);
    weekdays.add(Weekday.MONDAY);
    weekdays.add(Weekday.TUESDAY);
    weekdays.add(Weekday.WEDNESDAY);
    weekdays.add(Weekday.THURSDAY);
    weekdays.add(Weekday.FRIDAY);
    for (int day = 0; day < 5; day++) {
      for (int index = 0; index < amount; index++) {
        repo.save(new Timeslot(weekdays.get(day), amount, new ArrayList<>()));
      }
    }
  }

}
