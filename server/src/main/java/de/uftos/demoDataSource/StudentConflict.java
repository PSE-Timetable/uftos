package de.uftos.demoDataSource;

public record StudentConflict() implements PredefinedConstraint {
    @Override
    public PredefinedConstraints getPredefinedConstraints() {
        return PredefinedConstraints.STUDENT_CONFLICT;
    }
}
