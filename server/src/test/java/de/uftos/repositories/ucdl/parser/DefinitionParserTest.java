package de.uftos.repositories.ucdl.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.uftos.dto.ResourceType;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DefinitionParserTest {

  @Test
  void parseDefinition() {
    LinkedHashMap<String, ResourceType> parameters = new LinkedHashMap<>();

    parameters.put("this", ResourceType.TIMETABLE);
    parameters.put("grade", ResourceType.GRADE);
    parameters.put("room", ResourceType.ROOM);
    parameters.put("subject", ResourceType.SUBJECT);
    parameters.put("student", ResourceType.STUDENT);
    parameters.put("studentGroup", ResourceType.STUDENT_GROUP);
    parameters.put("tag", ResourceType.TAG);
    parameters.put("teacher", ResourceType.TEACHER);
    parameters.put("timeslot", ResourceType.TIMESLOT);


    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("true", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("false", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("(true)", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("true && true", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("true and true", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("true || true", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("true or true", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("!true", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("not true", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("true -> true", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("true implies true", parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student) {true}", parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("exists (x of student) {true}", parameters));


    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            student == student
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            student != student
            """, parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("5 == 5", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("5 != 5", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("5 < 5", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("5 <= 5", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("5 > 5", parameters));
    assertDoesNotThrow(() -> DefinitionParser.parseDefinition("5 >= 5", parameters));


    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            for (x : student) {
              if (true) {
                return true
              }
            }
            return true
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            for (x : student) {
              if (true) {
                return true
              }
              if (true) {
                return true
              }
            }
            return true
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            if (true) {
              if (true) {
                return true
              }
              if (true) {
                return true
              }
            }
            return true
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            student in studentGroup.students
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            isEmpty(studentGroup.students)
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            isEmpty([1,2,3])
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            size(studentGroup.students) == 0
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            0 in size(studentGroup.students)
            """, parameters));

    assertDoesNotThrow(() -> DefinitionParser.parseDefinition(
        """
            size([1,2,3]) == 0
            """, parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[true]) {true}", parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[true, true]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition(
            "forall (x : student[isEmpty(this.studentGroups)]) {true}", parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition(
            "forall (x : student[size(student.studentGroups) == 0]) {true}", parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[student]) {true}", parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[student in student]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[student == student]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[5 == 5 and true]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[5 == 5 or true]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[5 == 5 implies true]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student[isEmpty(student)]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition(
            "forall (x : student[forall (x : student[true]) {true}]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition(
            "forall (x : student[exists (x : student[true]) {true}]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition(
            "forall (x : student.studentGroups.lessons[this.teacher == teacher]) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition(
            "forall (x : [1,2,3][[1,2,3][1,2,3]]) {true}",
            parameters));


    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.lessons.index) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.lessons.teacher) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.lessons.timeslot) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.lessons.subject) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.lessons.room) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.lessons.studentGroup) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : studentGroup.grade) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : timeslot.day) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : timeslot.slot) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : studentGroup.students) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : tag.students) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.students) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : subject.teachers) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : tag.teachers) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.teachers) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : grade.studentGroups) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student.studentGroups) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : tag.studentGroups) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.studentGroups) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : tag.rooms) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.rooms) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : tag.subjects) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.subjects) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.subjects) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : tag.grades) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.grades) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : tag.timeslots) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.timeslots) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : room.lessons) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : subject.lessons) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : studentGroup.lessons) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.lessons) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.lessons) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : timeslot.lessons) {true}",
            parameters));

    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : grade.tags) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : room.tags) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : subject.tags) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : student.tags) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : studentGroup.tags) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : teacher.tags) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : this.tags) {true}",
            parameters));
    assertDoesNotThrow(
        () -> DefinitionParser.parseDefinition("forall (x : timeslot.tags) {true}",
            parameters));


    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("5 = 5",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("student < student",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("student == teacher",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition(
            "student in this.students[student in studentGroup.lessons]",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition(
            "isEmpty(student[[1,2,3][student]])",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition(
            "isEmpty(teacher[student])",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition(
            "isEmpty(student[[1,2,3].students])",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("""
                for (student : student) {
                  if (true) {return true}
                }
                return true
                """,
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("5 in student",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("5 in test",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(this : student) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(student : student) {true}",
            parameters));

    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.index) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.teacher) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.timeslot) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.room) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.subject) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.studentGroup) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.grade) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.day) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.slot) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.students) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.teachers) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : studentGroup.studentGroups) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.rooms) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.subjects) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.grades) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.timeslots) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : student.lessons) {true}",
            parameters));
    assertThrows(ParseException.class,
        () -> DefinitionParser.parseDefinition("forall(x : this.lessons.tags) {true}",
            parameters));


  }
}
