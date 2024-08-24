package de.uftos.repositories.solver.timefold.solver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.repositories.solver.timefold.constraints.ConstraintInstanceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimetableSolutionTimefoldInstance;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:MissingJavadocType")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ScoreCalculatorTest {

  @Test
  void calculateScore() {
    List<TimetableSolutionTimefoldInstance> timetables = getTimetables();
    HardSoftScore score;

    score = new ScoreCalculator().calculateScore(timetables.removeFirst());
    assertTrue(score.hardScore() == -1 && score.softScore() == 0);

    score = new ScoreCalculator().calculateScore(timetables.removeFirst());
    assertTrue(score.hardScore() == 0 && score.softScore() == 0);

    score = new ScoreCalculator().calculateScore(timetables.removeFirst());
    assertTrue(score.hardScore() == 0 && score.softScore() == -1);

    score = new ScoreCalculator().calculateScore(timetables.removeFirst());
    assertTrue(score.hardScore() == 0 && score.softScore() == 0);

    score = new ScoreCalculator().calculateScore(timetables.removeFirst());
    assertTrue(score.hardScore() == 0 && score.softScore() == 0);

    score = new ScoreCalculator().calculateScore(timetables.removeFirst());
    assertTrue(score.hardScore() == -1 && score.softScore() == 0);

    score = new ScoreCalculator().calculateScore(timetables.removeFirst());
    assertTrue(score.hardScore() == 0 && score.softScore() == 0);

    score = new ScoreCalculator().calculateScore(timetables.removeFirst());
    assertTrue(score.hardScore() == 0 && score.softScore() == -1);
  }

  private List<TimetableSolutionTimefoldInstance> getTimetables() {
    List<TimetableSolutionTimefoldInstance> timetables = new ArrayList<>();

    for (ConstraintInstanceTimefoldInstance constraintInstance : getConstraintInstances()) {
      TimetableSolutionTimefoldInstance timetable = new TimetableSolutionTimefoldInstance();
      timetable.getConstraintInstances().add(constraintInstance);
      timetables.add(timetable);

      timetable = new TimetableSolutionTimefoldInstance();
      ConstraintInstanceTimefoldInstance invertedEvaluation =
          new ConstraintInstanceTimefoldInstance(constraintInstance.arguments(),
              (params) -> !constraintInstance.evaluationFunction().apply(params),
              constraintInstance.rewardPenalize());
      timetable.getConstraintInstances().add(invertedEvaluation);
      timetables.add(timetable);
    }

    return timetables;
  }

  private List<ConstraintInstanceTimefoldInstance> getConstraintInstances() {
    List<ConstraintInstanceTimefoldInstance> instances = new ArrayList<>();
    instances.add(new ConstraintInstanceTimefoldInstance(new ArrayList<>(),
        (params) -> true,
        RewardPenalize.HARD_PENALIZE));
    instances.add(new ConstraintInstanceTimefoldInstance(new ArrayList<>(),
        (params) -> true,
        RewardPenalize.SOFT_PENALIZE));
    instances.add(new ConstraintInstanceTimefoldInstance(new ArrayList<>(),
        (params) -> true,
        RewardPenalize.HARD_REWARD));
    instances.add(new ConstraintInstanceTimefoldInstance(new ArrayList<>(),
        (params) -> true,
        RewardPenalize.SOFT_REWARD));
    return instances;
  }
}
