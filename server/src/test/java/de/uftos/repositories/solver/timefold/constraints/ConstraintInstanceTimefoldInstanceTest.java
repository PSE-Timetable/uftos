package de.uftos.repositories.solver.timefold.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.repositories.solver.timefold.domain.ResourceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.RoomTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.StudentTimefoldInstance;
import java.util.ArrayList;
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
public class ConstraintInstanceTimefoldInstanceTest {

  @Test
  void evaluate() {
    ConstraintInstanceTimefoldInstance instance;
    instance = new ConstraintInstanceTimefoldInstance(new ArrayList<>(), (l) -> true,
        RewardPenalize.HARD_PENALIZE);
    assertTrue(instance.evaluate(new HashMap<>()));
    instance = new ConstraintInstanceTimefoldInstance(new ArrayList<>(), (l) -> false,
        RewardPenalize.HARD_PENALIZE);
    assertFalse(instance.evaluate(new HashMap<>()));


    StudentTimefoldInstance student = new StudentTimefoldInstance("student");
    RoomTimefoldInstance room = new RoomTimefoldInstance("room");

    HashMap<String, ResourceTimefoldInstance> resources = new HashMap<>();
    resources.put(student.getId(), student);
    resources.put(room.getId(), room);

    List<ResourceTimefoldInstance> params = new ArrayList<>();
    List<ResourceType> types = new ArrayList<>();
    instance = new ConstraintInstanceTimefoldInstance(List.of(student, room), (l) -> {
      if (params.size() != types.size()) {
        return false;
      }
      for (int i = 0; i < types.size(); i++) {
        if (params.get(i).getResourceType() != types.get(i)) {
          return false;
        }
      }
      return true;
    }, RewardPenalize.HARD_PENALIZE);

    types.add(ResourceType.STUDENT);
    types.add(ResourceType.ROOM);
    params.add(student);

    assertFalse(instance.evaluate(resources));

    params.add(room);

    assertTrue(instance.evaluate(resources));

    types.set(1, ResourceType.TEACHER);

    assertFalse(instance.evaluate(resources));
  }

}
