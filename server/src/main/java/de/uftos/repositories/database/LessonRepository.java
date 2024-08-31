package de.uftos.repositories.database;

import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the lesson database table.
 */
public interface LessonRepository extends ListPagingAndSortingRepository<Lesson, String>,
    ListCrudRepository<Lesson, String> {

  @Query("SELECT l FROM lessons l WHERE l.timeslot IN :timeslots")
  List<Lesson> findAllByTimeslot(List<Timeslot> timeslots);

  @Query("SELECT l FROM lessons l WHERE l.teacher IN :teachers")
  List<Lesson> findAllByTeacher(List<Teacher> teachers);

  @Query("SELECT l FROM lessons l WHERE l.studentGroup IN :studentGroups")
  List<Lesson> findAllByStudentGroup(List<StudentGroup> studentGroups);

  @Query("SELECT l FROM lessons l WHERE l.room IN :rooms")
  List<Lesson> findAllByRoom(List<Room> rooms);

  @Query("SELECT l FROM lessons l WHERE l.subject IN :subjects")
  List<Lesson> findAllBySubject(List<Subject> subjects);
}
