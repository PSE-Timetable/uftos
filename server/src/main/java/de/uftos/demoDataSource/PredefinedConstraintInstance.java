package de.uftos.demoDataSource;

import de.uftos.entities.Resource;

import java.util.List;

public record PredefinedConstraintInstance(
        PredefinedConstraints name,
        List<Resource> parameters
) {}