package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table("students")
@Data
public class Student {
  @ManyToMany
  List<StudentGroup> groups;
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  private String firstName;
  private String lastName;
  @ManyToMany
  private List<Tag> tags;
}
