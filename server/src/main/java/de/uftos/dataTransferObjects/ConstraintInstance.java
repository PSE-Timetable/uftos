package de.uftos.dataTransferObjects;

import de.uftos.entities.Resource;

import java.util.List;

public record ConstraintInstance(
        String name,
        List<Resource> parameters
) {}
