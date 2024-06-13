package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
}
