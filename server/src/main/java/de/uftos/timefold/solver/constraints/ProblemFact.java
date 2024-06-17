package de.uftos.timefold.solver.constraints;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;
import java.util.stream.Stream;

public record ProblemFact(List<Object> parameters, Stream<String> definition, PenalizeReward punishReward , HardSoftScore score, String constraintName) {
    public boolean evaluate() {
        return true;
    }
}
