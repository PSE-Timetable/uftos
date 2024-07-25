package de.uftos.repositories.solver.timefold.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.calculator.EasyScoreCalculator;
import de.uftos.repositories.solver.timefold.constraints.constraintinstances.ConstraintInstanceTimefoldInstance;
import de.uftos.repositories.solver.timefold.domain.TimetableSolutionTimefoldInstance;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScoreCalculator
    implements EasyScoreCalculator<TimetableSolutionTimefoldInstance, HardSoftScore> {

  @Override
  public HardSoftScore calculateScore(
      TimetableSolutionTimefoldInstance timetableSolutionTimefoldInstance) {

    int i = (int) (Math.random() * Integer.MAX_VALUE);
    System.out.println("started " + i);

    List<ConstraintInstanceTimefoldInstance> constraints =
        timetableSolutionTimefoldInstance.getConstraintInstances();

    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(constraints.size());
    ExecutorService es =
        new ThreadPoolExecutor(1, (constraints.size() / 100) + 1, 1, TimeUnit.SECONDS,
            workQueue);

    List<Future<HardSoftScore>> futures = new ArrayList<>();

    List<List<ConstraintInstanceTimefoldInstance>> constraintLists = new ArrayList<>();

    for (int index = 0; index < constraints.size(); index += 100) {
      constraintLists.add(constraints.subList(index, Math.min(index + 100, constraints.size())));
    }

    for (List<ConstraintInstanceTimefoldInstance> constraintList : constraintLists) {
      futures.add(
          es.submit(() -> {
            int hard = 0;
            int soft = 0;
            for (ConstraintInstanceTimefoldInstance constraint : constraintList) {
              if (constraint.evaluate(timetableSolutionTimefoldInstance)) {
                switch (constraint.rewardPenalize()) {
                  case HARD_PENALIZE -> hard--;
                  case SOFT_PENALIZE -> soft--;
                  case HARD_REWARD -> hard++;
                  case SOFT_REWARD -> soft++;
                  default -> throw new IllegalStateException();
                }
              }
            }
            return HardSoftScore.of(hard, soft);
          }));
    }

    int hard = 0;
    int soft = 0;

    for (Future<HardSoftScore> future : futures) {
      HardSoftScore score;
      try {
        score = future.get();
      } catch (InterruptedException | ExecutionException e) {
        continue;
      }
      hard += score.hardScore();
      soft += score.softScore();

    }

    System.out.println("finished " + i + " (" + hard + ";" + soft + ")");

    return HardSoftScore.of(hard, soft);
  }

}
