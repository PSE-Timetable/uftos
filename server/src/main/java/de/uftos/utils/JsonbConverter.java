package de.uftos.utils;

import com.google.gson.Gson;
import de.uftos.entities.TimetableMetadata;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JsonbConverter implements AttributeConverter<TimetableMetadata, String> {
  private static final Gson GSON = new Gson();

  @Override
  public String convertToDatabaseColumn(TimetableMetadata object) {
    return GSON.toJson(object);
  }

  @Override
  public TimetableMetadata convertToEntityAttribute(String dbData) {
    return GSON.fromJson(dbData, TimetableMetadata.class);
  }
}
