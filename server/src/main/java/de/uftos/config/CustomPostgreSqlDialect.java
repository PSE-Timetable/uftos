package de.uftos.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;

/**
 * A custom sql dialect adding a new function for full text search.
 */
public class CustomPostgreSqlDialect extends PostgreSQLDialect {
  public CustomPostgreSqlDialect() {
    super();
  }

  @Override
  public void initializeFunctionRegistry(FunctionContributions functionContributions) {
    super.initializeFunctionRegistry(functionContributions);
    SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();
    functionRegistry.registerPattern(
        "tsvector_match",
        "(?1 @@ ?2)"
    );
  }
}
