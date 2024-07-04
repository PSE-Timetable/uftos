package de.uftos.builders;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
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
   *     OR filter added if the parameter is present.
   */
  public SpecificationBuilder<T> optionalOrEquals(Optional<String> param, String paramName) {
    if (param.isEmpty()) {
      return this;
    }
    specification =
        specification.or(
            ((root, query, cb) -> cb.like(root.get(paramName), "%" + param.get() + "%")));
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
   *     added if the attribute value array is present.
   */
  public SpecificationBuilder<T> optionalAndJoinIn(Optional<String[]> attributeValue,
                                                   String relationName, String attributeName) {
    if (attributeValue.isEmpty()) {
      return this;
    }
    specification = specification.and(
        (root, query, cb) -> root.join(relationName).get(attributeName)
            .in((Object[]) attributeValue.get())
    );
    return this;
  }

  public SpecificationBuilder<T> andEqual(String attributeValue, String attribute) {
    specification = specification.and((root, query, cb) -> cb.equal(root.get(attribute), attributeValue));
    return this;
  }

  public SpecificationBuilder<T> optionalAndJoinEqualsIgnoreCase(Optional<String> attributeValue,
                                                                 String relationName, String attributeName) {
    if (attributeValue.isEmpty()) {
      return this;
    }
    specification = specification.and(
        (root, query, cb) -> equalsIgnoreCase(cb, root.join(relationName).get(attributeName), attributeValue.get())
    );
    return this;
  }

  private Predicate equalsIgnoreCase(CriteriaBuilder cb, Expression<String> value, String pattern) {
    return cb.like(cb.lower(value), "%" + pattern.toLowerCase() + "%");
  }
}
