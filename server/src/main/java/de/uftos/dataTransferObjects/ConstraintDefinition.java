package de.uftos.dataTransferObjects;

import de.uftos.ucdl.AbstractSyntaxTree;

public record ConstraintDefinition(
        String name,
        AbstractSyntaxTree root
) {}
