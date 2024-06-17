package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "studentGroups")
@Data
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    @ManyToMany
    List<Student> students;
}
