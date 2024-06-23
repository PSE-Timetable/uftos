package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entity for lessons.
 * Contains an ID as well as an index. Also contains the teacher, student groups, room,
 * timeslot and subject of the lesson and the timetable of which the lesson is a
 * part of.
 */
@Entity(name = "lessons")
@Data
@NoArgsConstructor
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

  /**
   * Creates a new lesson.
   *
   * @param index          the index of the lesson.
   * @param teacherId      the id of the teacher that will hold the lesson.
   * @param studentGroupId the id of the student group that will attend the lesson.
   * @param roomId         the id of the room where the lesson will be hold.
   * @param timeslotId     the id of the timeslot of when the lesson will be hold.
   * @param subjectId      the id of the subject to be hold in the lesson.
   * @param timetableId    the id of the timetable.
   */
  public Lesson(int index, String teacherId, String studentGroupId, String roomId,
                String timeslotId, String subjectId, String timetableId) {
    // TODO implement
  }

}
