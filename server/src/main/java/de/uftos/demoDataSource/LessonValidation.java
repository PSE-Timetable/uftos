package de.uftos.demoDataSource;

public record LessonValidation() implements PredefinedConstraint {
    @Override
    public PredefinedConstraints getPredefinedConstraints() {
        return PredefinedConstraints.LESSON_VALIDATION;
    }
}
