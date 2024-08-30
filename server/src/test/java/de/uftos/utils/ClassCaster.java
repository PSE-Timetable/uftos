package de.uftos.utils;

import java.util.List;

@SuppressWarnings("checkstyle:MissingJavadocType")
public class ClassCaster {

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getClassType() {
    return (Class<T>) List.class;
  }
}
