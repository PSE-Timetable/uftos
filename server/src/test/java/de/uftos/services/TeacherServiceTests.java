package de.uftos.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.Grade;
import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.Server;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Teacher;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.TeacherRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TeacherServiceTests {

  @MockBean
  static TeacherRepository teacherRepository;
  @MockBean
  static ServerRepository serverRepository;

  static Room room1;
  static Room room2;

  //error: Autowired members must be defined in valid Spring bean (@Component|@Service|...)
  @Autowired
  private final TeacherService teacherService =
      new TeacherService(teacherRepository, serverRepository);

  @BeforeAll
  public static void setUp() {
    Teacher teacher1 = new Teacher("Max", "Mustermann", "MM",
        List.of("1", "2", "3"), List.of("1", "2", "3"));
    teacher1.setId("123");
    Student student1 = new Student();
    student1.setId("123");
    Student student2 = new Student();
    student1.setId("345");
    StudentGroup studentGroup1 = new StudentGroup();
    studentGroup1.setStudents(List.of(student1, student2));
    studentGroup1.setId("654");
    Student student3 = new Student();
    student3.setId("153");
    Student student4 = new Student();
    student4.setId("325");
    StudentGroup studentGroup2 = new StudentGroup();
    studentGroup2.setStudents(List.of(student1, student2));
    studentGroup2.setId("674");
    room1 = new Room();
    room1.setId("534");
    room2 = new Room();
    room2.setId("574");
    Grade grade1 = new Grade();
    grade1.setId("723");
    grade1.setStudentGroups(List.of(studentGroup1, studentGroup2));
    studentGroup1.setGrades(List.of(grade1));
    studentGroup2.setGrades(List.of(grade1));
    Lesson lesson1 = new Lesson();
    lesson1.setTeacher(teacher1);
    lesson1.setRoom(room1);
    lesson1.setStudentGroup(studentGroup1);
    lesson1.setYear("2024");

    Lesson lesson2 = new Lesson();
    lesson2.setTeacher(teacher1);
    lesson2.setRoom(room1);
    lesson2.setStudentGroup(studentGroup1);
    lesson2.setYear("2022");

    Lesson lesson3 = new Lesson();
    lesson3.setTeacher(teacher1);
    lesson3.setRoom(room2);
    lesson3.setStudentGroup(studentGroup2);
    lesson3.setYear("2024");

    teacher1.setLessons(List.of(lesson1, lesson2, lesson3));

    Mockito.when(serverRepository.findAll()).thenReturn(List.of(new Server(45, "2024")));
    Mockito.when(teacherRepository.findById("123")).thenReturn(Optional.of(teacher1));
  }

  @Test
  public void getLessonsByIdTest() {
    LessonResponseDto result = teacherService.getLessonsById("123");
    assertEquals(1, result.teachers().size());
    assertEquals(2, result.lessons().size());
    assertEquals(room1, result.rooms().getFirst());
    assertEquals(room2, result.rooms().get(1));
    assertEquals("654", result.grades().getFirst().studentGroupIds().getFirst());
    assertEquals("674", result.grades().getFirst().studentGroupIds().get(1));
    assertEquals("123", result.grades().getFirst().studentIds().getFirst());
    assertEquals("345", result.grades().getFirst().studentIds().get(1));
    fail();
  }


}
