package de.uftos.timefold.constraints;

import java.util.ArrayList;
import java.util.List;

public class ConstraintInstanceAggregate {
    private static final List<PredefinedConstraintInstance> INSTANCES = new ArrayList<>();

    public static void clearInstances() {
        INSTANCES.clear();
    }

    public static void addAllInstances(List<PredefinedConstraintInstance> instances) {
        INSTANCES.addAll(instances);
        System.out.println(INSTANCES.size());
    }

    public static List<PredefinedConstraintInstance> getInstances() {
        return new ArrayList<>(INSTANCES);
    }
}
