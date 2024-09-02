package de.uftos.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 * A builder class for constructing a JPA {@link Specification} object.
 *
 * @param <T> The type of the entity to which the specification applies.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
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
   * Adds a search filter to the specification if the provided parameter is present.
   *
   * @param text The text to search for
   * @return The current instance of {@code SpecificationBuilder} with the
   *       search filter added if the parameter is present.
   */
  public SpecificationBuilder<T> search(Optional<String> text) {
    if (text.isEmpty()) {
      return this;
    }

    if (text.get().isBlank()) {
      specification = specification.or((root, query, cb) -> cb.conjunction());
    }

    specification = specification.or((root, query, cb) -> cb.isTrue(
        cb.function("tsvector_match", Boolean.class, root.get("searchVector"),
            cb.function("websearch_to_tsquery", String.class, cb.literal("german"),
                cb.literal(text.get())))));

    return this;
  }

  /**
   * Adds an OR filter to the specification if the provided parameter is present.
   *
   * @param param     The optional parameter value to filter by.
   * @param paramName The name of the parameter to filter on.
   * @return The current instance of {@code SpecificationBuilder} with the
   *       OR filter added if the parameter is present.
   */
  public SpecificationBuilder<T> optionalOrLike(Optional<String> param, String paramName) {
    if (param.isEmpty()) {
      return this;
    }
    specification =
        specification.or(
            ((root, query, cb) -> likeIgnoreCase(cb, root.get(paramName), param.get()))
        );
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
   *       added if the attribute value array is present.
   */
  public SpecificationBuilder<T> optionalAndJoinIn(Optional<String[]> attributeValue,
                                                   String relationName, String attributeName) {
    if (attributeValue.isEmpty()) {
      return this;
    }
    specification = specification.and(
        (root, query, cb) -> root
            .join(relationName)
            .get(attributeName)
            .in(Arrays.stream(attributeValue.get()).toList())
    );
    return this;
  }

  /**
   * Adds a filter to the specification by joining two times with another relation.
   *
   * @param attributeValue     The optional array of attribute values to filter by.
   * @param firstRelationName  The name of the first relation to join.
   * @param secondRelationName The name of the second relation to join.
   * @param attributeName      The name of the attribute in the joined relation to filter on.
   * @return The current instance of {@code SpecificationBuilder} with the join filter
   *       added if the attribute value array is present.
   */
  public SpecificationBuilder<T> andDoubleJoinIn(String[] attributeValue, String firstRelationName,
                                                 String secondRelationName, String attributeName) {
    specification = specification.and(
        (root, query, cb) -> root
            .join(firstRelationName)
            .join(secondRelationName)
            .get(attributeName)
            .in(Arrays.stream(attributeValue).toList())
    );
    return this;
  }

  private Predicate likeIgnoreCase(CriteriaBuilder cb, Expression<String> value, String pattern) {
    return cb.like(cb.lower(value), "%" + pattern.toLowerCase() + "%");
  }
}
