package de.uftos.utils;

import java.util.List;

/**
 * Utility class to return the type of any class.
 * Used when mocking a method that returns a
 * <pre>
 * {@code
 * List<T> list
 * }
 * </pre>.
 */
public class ClassCaster {

  /**
   * Returns a class of type
   * <pre>
   *  {@code
   *  List<T> list
   *  }
   *  </pre>.
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> getClassType() {
    return (Class<T>) List.class;
  }
}
