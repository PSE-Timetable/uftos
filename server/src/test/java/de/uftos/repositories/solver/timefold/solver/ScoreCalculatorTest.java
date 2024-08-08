package de.uftos.repositories.solver.timefold.solver;

import de.uftos.repositories.solver.timefold.constraints.ConstraintInstanceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.RoomTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimetableSolutionTimefoldInstance;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ScoreCalculatorTest {

  @BeforeEach
  void setUp() {
  }


  @Test
  void calculateScore() {
  }

  private List<TimetableSolutionTimefoldInstance> getTimetables() {
    List<TimetableSolutionTimefoldInstance> timetables = new ArrayList<>();

    for (ConstraintInstanceTimefoldInstance instance : getConstraintInstances()) {
      TimetableSolutionTimefoldInstance timetable = getTimetableWithoutConstraints();
      timetable.getConstraintInstances().add(instance);
      timetables.add(timetable);
    }

    return timetables;
  }

  private TimetableSolutionTimefoldInstance getTimetableWithoutConstraints() {
    TimetableSolutionTimefoldInstance timetable = new TimetableSolutionTimefoldInstance();
    RoomTimefoldInstance room1 = new RoomTimefoldInstance("room1");
    RoomTimefoldInstance room2 = new RoomTimefoldInstance("room1");
    RoomTimefoldInstance room3 = new RoomTimefoldInstance("room1");

    StudentTimefoldInstance student1 = new StudentTimefoldInstance("student1");
    StudentTimefoldInstance student2 = new StudentTimefoldInstance("student2");
    StudentTimefoldInstance student3 = new StudentTimefoldInstance("student3");
    return timetable;
  }

  private List<ConstraintInstanceTimefoldInstance> getConstraintInstances() {
    return null;
  }
}
