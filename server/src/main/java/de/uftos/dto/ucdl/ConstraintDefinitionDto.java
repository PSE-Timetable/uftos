package de.uftos.dto.ucdl;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import java.util.LinkedHashMap;

/**
 * A parsed constraint.
 *
 * @param name       the unique name of the constraint definition.
 * @param parameters a Hashmap containing the unique names of the parameters and their type.
 * @param root       the root of the abstract syntax tree describing the definition.
 */
public record ConstraintDefinitionDto(
    String name,
    String description,
    RewardPenalize defaultType,
    LinkedHashMap<String, ResourceType> parameters,
    AbstractSyntaxTreeDto root
) {
}
