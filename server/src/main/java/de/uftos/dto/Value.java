package de.uftos.dto;

import de.uftos.repositories.parser.Token;

public record Value<T>(
        Token type,
        T value
) implements AbstractSyntaxTree {
}
