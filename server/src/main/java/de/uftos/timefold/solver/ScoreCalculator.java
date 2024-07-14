package de.uftos.timefold.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.calculator.EasyScoreCalculator;
import de.uftos.timefold.domain.TimetableSolutionTimefoldInstance;

public class ScoreCalculator
    implements EasyScoreCalculator<TimetableSolutionTimefoldInstance, HardSoftScore> {

  @Override
  public HardSoftScore calculateScore(
      TimetableSolutionTimefoldInstance timetableSolutionTimefoldInstance) {
    //todo: calculate score
    return HardSoftScore.of(0, 0);
  }
}
