package de.uftos.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.PostgreSQLDialect;

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
    var functionRegistry = functionContributions.getFunctionRegistry();
    functionRegistry.registerPattern(
        "tsvector_match",
        "(?1 @@ ?2)"
    );
  }
}
