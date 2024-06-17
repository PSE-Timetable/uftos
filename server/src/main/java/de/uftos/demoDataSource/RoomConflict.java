package de.uftos.demoDataSource;

public record RoomConflict() implements PredefinedConstraint {
    @Override
    public PredefinedConstraints getPredefinedConstraints() {
        return PredefinedConstraints.ROOM_CONFLICT;
    }
}
