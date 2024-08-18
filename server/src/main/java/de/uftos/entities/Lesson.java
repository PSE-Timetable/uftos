package de.uftos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
  @NotEmpty
  private String id;

  @PositiveOrZero
  @NotNull
  private int index;

  @NotEmpty
  private String year;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "teachers_id", nullable = false)
  private Teacher teacher;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "student_groups_id", nullable = false)
  private StudentGroup studentGroup;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "rooms_id", nullable = false)
  private Room room;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "timeslots_id", nullable = false)
  private Timeslot timeslot;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "subjects_id", nullable = false)
  private Subject subject;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "timetables", nullable = false)
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
   * @param year           the year of the timetable.
   */
  public Lesson(int index, String teacherId, String studentGroupId, String roomId,
                String timeslotId, String subjectId, String timetableId, String year) {
    this.index = index;
    this.teacher = new Teacher(teacherId);
    this.studentGroup = new StudentGroup(studentGroupId);
    this.room = new Room(roomId);
    this.timeslot = new Timeslot(timeslotId);
    this.subject = new Subject(subjectId);
    this.timetable = new Timetable(timetableId);
    this.year = year;
  }



  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Lesson that = (Lesson) other;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int initialOddNumber = 43;
    int multiplierOddNumber = 107;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(index)
        .append(year)
        .toHashCode();
  }
}
