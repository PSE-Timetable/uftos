package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table("rooms")
@Data
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;
  private String buildingName;
  private int capacity;

  @ManyToMany
  private List<Tag> tags;

  @OneToMany
  private List<Lesson> lessons;
}
