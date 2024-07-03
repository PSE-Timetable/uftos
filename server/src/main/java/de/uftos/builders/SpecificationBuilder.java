package de.uftos.builders;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 * A builder class for constructing a JPA {@link Specification} object.
 *
 * @param <T> The type of the entity to which the specification applies.
 */
public class SpecificationBuilder<T> {

  private Specification<T> specification;

  /**
   * Constructs a new {@code SpecificationBuilder} with an empty specification.
   */
  public SpecificationBuilder() {
    specification = Specification.where(null);
  }

  /**
   * Builds and returns the constructed {@link Specification}.
   *
   * @return The constructed specification.
   */
  public Specification<T> build() {
    return specification;
  }

  /**
   * Adds an OR filter to the specification if the provided parameter is present.
   *
   * @param param     The optional parameter value to filter by.
   * @param paramName The name of the parameter to filter on.
   * @return The current instance of {@code SpecificationBuilder} with the
   *        OR filter added if the parameter is present.
   */
  public SpecificationBuilder<T> addOptionalOr(Optional<String> param, String paramName) {
    if (param.isEmpty()) {
      return this;
    }
    specification = specification.or(createOrFilter(param.get(), paramName));
    return this;
  }

  /**
   * Adds a filter to the specification by joining with another relation if the
   * provided attribute value array is present.
   *
   * @param attributeValue The optional array of attribute values to filter by.
   * @param relationName   The name of the relation to join.
   * @param attributeName  The name of the attribute in the joined relation to filter on.
   * @return The current instance of {@code SpecificationBuilder} with the join filter
   *        added if the attribute value array is present.
   */
  public SpecificationBuilder<T> addOptionalJoinFilter(Optional<String[]> attributeValue,
                                                       String relationName, String attributeName) {
    if (attributeValue.isEmpty()) {
      return this;
    }
    specification = specification.and(
        createJoinFilter(relationName, attributeName, List.of(attributeValue.get())));
    return this;
  }

  /**
   * Creates an OR filter specification for the given parameter name and value.
   *
   * @param param     The value to filter by.
   * @param paramName The name of the parameter to filter on.
   * @return A specification representing the OR filter.
   */
  private Specification<T> createOrFilter(String param, String paramName) {
    return ((root, query, cb) -> cb.like(root.get(paramName), "%" + param + "%"));
  }

  /**
   * Creates a filter specification by joining with the given relation and
   * filtering on the given attribute.
   *
   * @param relationName    The name of the relation to join.
   * @param attributeName   The name of the attribute in the joined relation to filter on.
   * @param attributeValues The list of attribute values to filter by.
   * @return A specification representing the join filter.
   */
  private Specification<T> createJoinFilter(String relationName, String attributeName,
                                            List<String> attributeValues) {
    return (root, query, cb) -> root.join(relationName).get(attributeName).in(attributeValues);
  }
}