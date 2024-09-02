package de.uftos.repositories.solver.timefold.constraints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.GradeProblemDto;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.solver.RoomProblemDto;
import de.uftos.dto.solver.StudentProblemDto;
import de.uftos.repositories.solver.timefold.domain.RoomTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimetableSolutionTimefoldInstance;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConstraintInstanceFactoryTest {

  @Test
  void getConstraintInstance() {
    ConstraintDefinitionTimefoldInstance definition =
        new ConstraintDefinitionTimefoldInstance("test", RewardPenalize.HARD_PENALIZE, (l) -> true);
    HashMap<String, ConstraintDefinitionTimefoldInstance> definitions = new HashMap<>();
    definitions.put(definition.name(), definition);

    TimetableSolutionTimefoldInstance timetable = new TimetableSolutionTimefoldInstance();
    timetable.getStudents().add(new StudentTimefoldInstance("student"));
    timetable.getRooms().add(new RoomTimefoldInstance("room"));

    StudentProblemDto student = new StudentProblemDto("student", null, null);
    RoomProblemDto room = new RoomProblemDto("room", null, null);

    ConstraintInstanceDto dto;
    ConstraintInstanceTimefoldInstance instance;

    dto = new ConstraintInstanceDto("test", null, List.of(student, room));
    instance =
        ConstraintInstanceFactory.getConstraintInstance(dto, definitions, timetable.getResources());
    assertEquals(instance.arguments().size(), dto.parameters().size() + 1);
    assertEquals(instance.rewardPenalize(), RewardPenalize.HARD_PENALIZE);
    assertTrue(instance.evaluate(timetable.getResources()));
    for (int i = 0; i < instance.arguments().size(); i++) {
      if (i != 0) {
        assertEquals(instance.arguments().get(i).getId(), dto.parameters().get(i - 1).id());
      }
      assertSame(instance.arguments().get(i),
          timetable.getResources().get((instance.arguments().get(i).getId())));
    }

    dto = new ConstraintInstanceDto("test", RewardPenalize.HARD_PENALIZE, List.of(student, room));
    instance =
        ConstraintInstanceFactory.getConstraintInstance(dto, definitions, timetable.getResources());
    assertEquals(instance.arguments().size(), dto.parameters().size() + 1);
    assertEquals(instance.rewardPenalize(), RewardPenalize.HARD_PENALIZE);
    assertTrue(instance.evaluate(timetable.getResources()));
    for (int i = 0; i < instance.arguments().size(); i++) {
      if (i != 0) {
        assertEquals(instance.arguments().get(i).getId(), dto.parameters().get(i - 1).id());
      }
      assertSame(instance.arguments().get(i),
          timetable.getResources().get((instance.arguments().get(i).getId())));
    }

    dto = new ConstraintInstanceDto("test", RewardPenalize.SOFT_PENALIZE, List.of(student, room));
    instance =
        ConstraintInstanceFactory.getConstraintInstance(dto, definitions, timetable.getResources());
    assertEquals(instance.arguments().size(), dto.parameters().size() + 1);
    assertEquals(instance.rewardPenalize(), RewardPenalize.SOFT_PENALIZE);
    assertTrue(instance.evaluate(timetable.getResources()));
    for (int i = 0; i < instance.arguments().size(); i++) {
      if (i != 0) {
        assertEquals(instance.arguments().get(i).getId(), dto.parameters().get(i - 1).id());
      }
      assertSame(instance.arguments().get(i),
          timetable.getResources().get((instance.arguments().get(i).getId())));
    }

    dto = new ConstraintInstanceDto("test", RewardPenalize.HARD_REWARD, List.of(student, room));
    instance =
        ConstraintInstanceFactory.getConstraintInstance(dto, definitions, timetable.getResources());
    assertEquals(instance.arguments().size(), dto.parameters().size() + 1);
    assertEquals(instance.rewardPenalize(), RewardPenalize.HARD_REWARD);
    assertTrue(instance.evaluate(timetable.getResources()));
    for (int i = 0; i < instance.arguments().size(); i++) {
      if (i != 0) {
        assertEquals(instance.arguments().get(i).getId(), dto.parameters().get(i - 1).id());
      }
      assertSame(instance.arguments().get(i),
          timetable.getResources().get((instance.arguments().get(i).getId())));
    }

    dto = new ConstraintInstanceDto("test", RewardPenalize.SOFT_REWARD, List.of(student, room));
    instance =
        ConstraintInstanceFactory.getConstraintInstance(dto, definitions, timetable.getResources());
    assertEquals(instance.arguments().size(), dto.parameters().size() + 1);
    assertEquals(instance.rewardPenalize(), RewardPenalize.SOFT_REWARD);
    assertTrue(instance.evaluate(timetable.getResources()));
    for (int i = 0; i < instance.arguments().size(); i++) {
      if (i != 0) {
        assertEquals(instance.arguments().get(i).getId(), dto.parameters().get(i - 1).id());
      }
      assertSame(instance.arguments().get(i),
          timetable.getResources().get((instance.arguments().get(i).getId())));
    }


    GradeProblemDto wrongStudent = new GradeProblemDto("student", null, null);

    dto = new ConstraintInstanceDto("test", RewardPenalize.HARD_PENALIZE,
        List.of(wrongStudent, room));
    ConstraintInstanceDto finalDto = dto;
    assertThrows(IllegalArgumentException.class,
        () -> ConstraintInstanceFactory.getConstraintInstance(
            finalDto, definitions, timetable.getResources()));
  }
}
