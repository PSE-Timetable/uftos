package de.uftos.repositories.solver.timefold.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uftos.dto.ResourceType;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TimetableSolutionTimefoldInstanceTest {

  @Test
  void getResourcesType() {
    TimetableSolutionTimefoldInstance timetable = new TimetableSolutionTimefoldInstance();
    assertEquals(timetable.getResourceType(), ResourceType.TIMETABLE);
  }

  @Test
  void timetableId() {
    assertEquals(new TimetableSolutionTimefoldInstance().getId(), "this");
  }

  @Test
  void getResources() {
    TimetableSolutionTimefoldInstance timetable = new TimetableSolutionTimefoldInstance();

    GradeTimefoldInstance grade = new GradeTimefoldInstance("grade");
    timetable.getGrades().add(grade);
    RoomTimefoldInstance room = new RoomTimefoldInstance("room");
    timetable.getRooms().add(room);
    StudentGroupTimefoldInstance studentGroup = new StudentGroupTimefoldInstance("studentGroup");
    timetable.getStudentGroups().add(studentGroup);
    StudentTimefoldInstance student = new StudentTimefoldInstance("student");
    timetable.getStudents().add(student);
    SubjectTimefoldInstance subject = new SubjectTimefoldInstance("subject");
    timetable.getSubjects().add(subject);
    TagTimefoldInstance tag = new TagTimefoldInstance("tag");
    timetable.getTags().add(tag);
    TeacherTimefoldInstance teacher = new TeacherTimefoldInstance("teacher");
    timetable.getTeachers().add(teacher);
    TimeslotTimefoldInstance timeslot = new TimeslotTimefoldInstance("timeslot");
    timetable.getTimeslots().add(timeslot);

    LessonTimefoldInstance lesson = new LessonTimefoldInstance("lesson");
    timetable.getLessons().add(lesson);

    HashMap<String, ResourceTimefoldInstance> resources = timetable.getResources();

    assertEquals(resources.size(), 10);

    assertTrue(resources.containsKey("this"));
    assertSame(resources.get("this"), timetable);

    assertTrue(resources.containsKey("grade"));
    assertSame(resources.get("grade"), grade);

    assertTrue(resources.containsKey("room"));
    assertSame(resources.get("room"), room);

    assertTrue(resources.containsKey("studentGroup"));
    assertSame(resources.get("studentGroup"), studentGroup);

    assertTrue(resources.containsKey("student"));
    assertSame(resources.get("student"), student);

    assertTrue(resources.containsKey("subject"));
    assertSame(resources.get("subject"), subject);

    assertTrue(resources.containsKey("tag"));
    assertSame(resources.get("tag"), tag);

    assertTrue(resources.containsKey("teacher"));
    assertSame(resources.get("teacher"), teacher);

    assertTrue(resources.containsKey("timeslot"));
    assertSame(resources.get("timeslot"), timeslot);

    assertTrue(resources.containsKey("lesson"));
    assertSame(resources.get("lesson"), lesson);
  }
}
