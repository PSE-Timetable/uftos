package de.uftos.repositories.solver.timefold.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.uftos.dto.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TimeslotTimefoldInstanceTest {

  @Test
  void getResourcesType() {
    TimeslotTimefoldInstance timeslot = new TimeslotTimefoldInstance("test");
    assertEquals(timeslot.getResourceType(), ResourceType.TIMESLOT);
  }

  @Test
  void timeslotWithSlotAndDay() {
    TimeslotTimefoldInstance timeslot;

    timeslot = new TimeslotTimefoldInstance("test", 0, 0);
    assertEquals(timeslot.getDayOfWeek(), 0);
    assertEquals(timeslot.getSlotId(), 0);

    timeslot = new TimeslotTimefoldInstance("test", Integer.MIN_VALUE, Integer.MAX_VALUE);
    assertEquals(timeslot.getDayOfWeek(), Integer.MIN_VALUE);
    assertEquals(timeslot.getSlotId(), Integer.MAX_VALUE);

    timeslot = new TimeslotTimefoldInstance("test", Integer.MAX_VALUE, Integer.MIN_VALUE);
    assertEquals(timeslot.getDayOfWeek(), Integer.MAX_VALUE);
    assertEquals(timeslot.getSlotId(), Integer.MIN_VALUE);
  }
}
