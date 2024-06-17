package de.uftos.ucdl;

import java.util.List;

public record AbstractSyntaxTree(
        Token name,
        List<AbstractSyntaxTree> children,
        Object value
) {}
