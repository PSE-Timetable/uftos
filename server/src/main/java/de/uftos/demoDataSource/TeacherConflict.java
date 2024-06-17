package de.uftos.demoDataSource;

public record TeacherConflict() implements PredefinedConstraint {
    @Override
    public PredefinedConstraints getPredefinedConstraints() {
        return PredefinedConstraints.TEACHER_CONFLICT;
    }
}
