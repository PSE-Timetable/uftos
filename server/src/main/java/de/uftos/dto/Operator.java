package de.uftos.dto;

import de.uftos.repositories.parser.Token;

import java.util.List;

public record Operator(
        Token type,
        List<AbstractSyntaxTree> parameters
) implements AbstractSyntaxTree {
}
