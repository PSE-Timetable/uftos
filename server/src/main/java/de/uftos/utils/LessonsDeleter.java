package de.uftos.utils;

import de.uftos.entities.Lesson;
import de.uftos.entities.Room;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Teacher;
import de.uftos.entities.Timeslot;
import de.uftos.entities.Timetable;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.TimetableRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Delete all lessons that contain all given entities.
 */
public class LessonsDeleter {

  private final LessonRepository lessonRepository;
  private final TimetableRepository timetableRepository;

  /**
   * Creates the lesson deleter.
   *
   * @param lessonRepository    the repository for accessing the lesson table.
   * @param timetableRepository the repository for accessing the timetable table.
   */
  public LessonsDeleter(LessonRepository lessonRepository,
                        TimetableRepository timetableRepository) {
    this.lessonRepository = lessonRepository;
    this.timetableRepository = timetableRepository;
  }

  /**
   * Deletes all lessons that use these teachers and all timetables tha
   * use these lessons.
   *
   * @param teachers the teachers for which the lessons have to be deleted.
   */
  public void fromTeachers(List<Teacher> teachers) {
    List<Lesson> lessons = this.lessonRepository.findAllByTeacher(teachers);
    deleteLessonsAndTimetables(lessons);
  }

  /**
   * Deletes all lessons that use these subjects and all timetables tha
   * use these lessons.
   *
   * @param subjects the subjects for which the lessons have to be deleted.
   */
  public void fromSubjects(List<Subject> subjects) {
    List<Lesson> lessons = this.lessonRepository.findAllBySubject(subjects);
    deleteLessonsAndTimetables(lessons);
  }

  /**
   * Deletes all lessons that use these rooms and all timetables tha
   * use these lessons.
   *
   * @param rooms the rooms for which the lessons have to be deleted.
   */
  public void fromRooms(List<Room> rooms) {
    List<Lesson> lessons = this.lessonRepository.findAllByRoom(rooms);
    deleteLessonsAndTimetables(lessons);
  }


  /**
   * Deletes all lessons that use these student groups and all timetables tha
   * use these lessons.
   *
   * @param studentGroups the studentGroups for which the lessons have to be deleted.
   */
  public void fromStudentGroups(List<StudentGroup> studentGroups) {
    List<Lesson> lessons = this.lessonRepository.findAllByStudentGroup(studentGroups);
    deleteLessonsAndTimetables(lessons);
  }

  /**
   * Deletes all lessons that use these timeslots and all timetables tha
   * use these lessons.
   *
   * @param timeslots the timeslots for which the lessons have to be deleted.
   */
  public void fromTimeSlots(List<Timeslot> timeslots) {
    List<Lesson> lessons = this.lessonRepository.findAllByTimeslot(timeslots);
    deleteLessonsAndTimetables(lessons);
  }

  private void deleteLessonsAndTimetables(List<Lesson> lessons) {
    if (lessons.isEmpty()) {
      return;
    }
    List<Timetable> timetables = this.timetableRepository.findAllByLessons(
        lessons.stream().map(Lesson::getId).toList()
    );
    Set<Lesson> lessonsToDelete = new HashSet<>(lessons);

    // Adding all lessons from the timetables to be deleted
    timetables.forEach(timetable -> lessonsToDelete.addAll(timetable.getLessons()));
    this.lessonRepository.deleteAll(lessonsToDelete);

    this.timetableRepository.deleteAll(timetables);
  }
}
