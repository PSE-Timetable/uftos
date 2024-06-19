package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "grades")
@Data
@NoArgsConstructor
public class Grade {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  @ManyToMany
  private List<StudentGroup> studentGroups;

  @ManyToMany
  private List<Tag> tags;

  public Grade(String id) {
    this.id = id;
  }

  public Grade(String name, List<String> studentGroupIds, List<String> tagIds) {
    this.name = name;
    this.studentGroups = studentGroupIds.stream().map(StudentGroup::new).toList();
    this.tags = tagIds.stream().map(Tag::new).toList();
  }
}
