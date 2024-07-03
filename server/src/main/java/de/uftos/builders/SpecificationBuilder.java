package de.uftos.builders;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {

  private Specification<T> specification;

  public SpecificationBuilder() {
    specification = Specification.where(null);
  }

  public Specification<T> build() {
    return specification;
  }

  public SpecificationBuilder<T> addOptionalOr(Optional<String> param, String paramName) {
    if (param.isEmpty()) return this;
    specification = specification.or(createOrFilter(param.get(), paramName));
    return this;
  }

  public SpecificationBuilder<T> addOptionalJoinFilter(Optional<String[]> attributeValue, String relationName, String attributeName) {
    if (attributeValue.isEmpty()) return this;
    specification = specification.and(createJoinFilter(relationName, attributeName, List.of(attributeValue.get())));
    return this;
  }

  private Specification<T> createOrFilter(String param, String paramName) {
    return ((root, query, cb) ->  cb.like(root.get(paramName), "%" + param + "%"));
  }

  private Specification<T> createJoinFilter(String relationName, String attributeName, List<String> attributeValues) {
    return (root, query, cb) -> {
      // Create a predicate to check if the tag ID matches
      return root.join(relationName).get(attributeName).in(attributeValues);
    };
  }
}
