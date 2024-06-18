package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity(name = "lessons")
@Data
public class Lesson {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private int index;

  @ManyToOne
  private Teacher teacher;

  @ManyToOne
  private StudentGroup studentGroup;

  @ManyToOne
  private Room room;

  @ManyToOne
  private Timeslot timeslot;

  @ManyToOne
  private Subject subject;

  @ManyToOne
  private Timetable timetable;
}
