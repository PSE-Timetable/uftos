package de.uftos.dto.parser;

import de.uftos.dto.ResourceType;

public record FilterDto(SemanticToken token, String variableName, ResourceType variableType, AbstractSyntaxTreeDto body) implements AbstractSyntaxTreeDto {

  @Override
  public SemanticToken getToken() {
    return this.token;
  }

  @Override
  public String toString() {
    return this.getToken() +
        ";" +
        this.variableName +
        ";" +
        this.variableType +
        ";{" +
        this.body +
        "}";
  }
}
