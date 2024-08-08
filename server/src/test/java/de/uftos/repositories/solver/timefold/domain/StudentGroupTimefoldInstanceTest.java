package de.uftos.repositories.solver.timefold.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.uftos.dto.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudentGroupTimefoldInstanceTest {

  @Test
  void getResourcesType() {
    StudentGroupTimefoldInstance studentGroup = new StudentGroupTimefoldInstance("test");
    assertEquals(studentGroup.getResourceType(), ResourceType.STUDENT_GROUP);
  }

  @Test
  void studentGroupWithGrade() {
    GradeTimefoldInstance grade = new GradeTimefoldInstance("grade");
    StudentGroupTimefoldInstance studentGroup =
        new StudentGroupTimefoldInstance("studentGroup", grade);
    assertSame(studentGroup.getGrade(), grade);
  }
}
