package de.uftos.services;

import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.*;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.ServerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GradeServiceTests {
    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private ServerRepository serverRepository;

    @InjectMocks
    private GradeService gradeService;

    private Room room1;
    private Room room2;

    private static void assertResulArraysSizes(LessonResponseDto result, int teachers, int lessons,
                                               int rooms, int grades) {
        assertAll("Testing whether the sizes of the arrays are correct",
                () -> assertEquals(teachers, result.teachers().size()),
                () -> assertEquals(lessons, result.lessons().size()),
                () -> assertEquals(rooms, result.rooms().size()),
                () -> assertEquals(grades, result.grades().size())
        );
    }

    @BeforeEach
    void setUp() {
        StudentGroup studentGroup1 = new StudentGroup("5-Ethik", List.of("S1", "S2"), List.of(), List.of());
        studentGroup1.setId("g123");

        StudentGroup studentGroup2 = new StudentGroup("5-Religion", List.of("S2", "S3"), List.of(), List.of("T1", "T2"));
        studentGroup2.setId("g456");

        StudentGroup studentGroup3 = new StudentGroup("7-Ethik", List.of("S1", "S2"), List.of(), List.of());
        studentGroup1.setId("g234");

        StudentGroup studentGroup4 = new StudentGroup("7-Religion", List.of("S2", "S3"), List.of(), List.of("T1", "T2"));
        studentGroup2.setId("g567");

        Teacher teacher1 = new Teacher("T1");
        Teacher teacher2 = new Teacher("T2");

        Grade grade1 = new Grade("5", List.of(studentGroup1.getName(), studentGroup2.getName()), List.of("T1", "T2"));
        grade1.setId("123");
        Grade grade2 = new Grade("7", List.of(studentGroup3.getName(), studentGroup4.getName()), List.of("T2", "T3"));
        grade2.setId("456");
        studentGroup1.setGrades(List.of(grade1));
        studentGroup2.setGrades(List.of(grade1));

        Subject subject = new Subject("789");

        room1 = new Room("534");
        room2 = new Room("574");

        // TODO lesson constructor with student group array as parameter, look at grade entity studentgroups

        Lesson lesson1 = createLesson(teacher1, room1, studentGroup1, "2024", subject);
        Lesson lesson2 = createLesson(teacher2, room1, studentGroup1, "2022", subject);
        Lesson lesson3 = createLesson(teacher1, room2, studentGroup2, "2024", subject);
        Lesson lesson4 = createLesson(teacher1, room2, studentGroup2, "2022", subject);
        Lesson lesson5 = createLesson(teacher1, room2, studentGroup2, "2024", subject);

        studentGroup1.setLessons(List.of(lesson1, lesson2, lesson3));
        studentGroup2.setLessons(List.of(lesson3, lesson4, lesson5));

        Server server = new Server(45, "2024");
        when(serverRepository.findAll()).thenReturn(List.of(server));
        when(gradeRepository.findById("123")).thenReturn(Optional.of(grade1));
        when(gradeRepository.findById("456")).thenReturn(Optional.of(grade2));
    }






    private Lesson createLesson(Teacher teacher, Room room, StudentGroup studentGroup,
                                String number,
                                Subject subject) {
        Lesson lesson = new Lesson();
        lesson.setTeacher(teacher);
        lesson.setRoom(room);
        lesson.setStudentGroup(studentGroup);
        lesson.setYear(number);
        lesson.setSubject(subject);
        return lesson;
    }
}
