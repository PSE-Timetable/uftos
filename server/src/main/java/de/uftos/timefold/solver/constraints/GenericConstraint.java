package de.uftos.timefold.solver.constraints;

import ai.timefold.solver.core.api.score.constraint.ConstraintRef;
import ai.timefold.solver.core.impl.score.stream.common.AbstractConstraint;
import ai.timefold.solver.core.impl.score.stream.common.InnerConstraintFactory;
import ai.timefold.solver.core.impl.score.stream.common.ScoreImpactType;

import java.util.function.Function;

public class GenericConstraint extends AbstractConstraint {
    protected GenericConstraint(InnerConstraintFactory constraintFactory, ConstraintRef constraintRef, Function constraintWeightExtractor, ScoreImpactType scoreImpactType,
                                boolean isConstraintWeightConfigurable, Object justificationMapping, Object indictedObjectsMapping) {
        super(constraintFactory, constraintRef, constraintWeightExtractor, scoreImpactType, isConstraintWeightConfigurable, justificationMapping,
                indictedObjectsMapping);
    }
}
