package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "grades")
@Data
public class Grade {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private long id;

  private String name;

  @ManyToMany
  private List<StudentGroup> studentGroups;

  @ManyToMany
  private List<Tag> tags;
}
