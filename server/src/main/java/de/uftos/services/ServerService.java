package de.uftos.services;

import de.uftos.dto.Weekday;
import de.uftos.dto.responsedtos.ServerStatisticsResponseDto;
import de.uftos.entities.Server;
import de.uftos.entities.Timeslot;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.LessonRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.repositories.database.TimetableRepository;
import de.uftos.utils.ConstraintInstanceDeleter;
import de.uftos.utils.LessonsDeleter;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The service providing the logic of the /server endpoint.
 */
@Service
public class ServerService {
  private final ServerRepository repository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final GradeRepository gradeRepository;
  private final RoomRepository roomRepository;
  private final ConstraintInstanceRepository constraintInstanceRepository;
  private final ConstraintSignatureRepository constraintSignatureRepository;
  private final TimeslotRepository timeslotRepository;
  private final TimetableRepository timetableRepository;
  private final LessonRepository lessonRepository;

  /**
   * Creates a server service.
   *
   * @param repository                    the repository for accessing the server table.
   * @param studentRepository             the repository for accessing the student table.
   * @param teacherRepository             the repository for accessing the teacher table.
   * @param gradeRepository               the repository for accessing the student group table.
   * @param roomRepository                the repository for accessing the room table.
   * @param constraintInstanceRepository  the repository for accessing the constraint instance table.
   * @param constraintSignatureRepository the repository for accessing the constraint signature table.
   * @param timeslotRepository            the repository for accessing the timeslot table.
   * @param timetableRepository           the repository for accessing the timetable table.
   * @param lessonRepository              the repository for accessing the lesson table.
   */
  @Autowired
  public ServerService(ServerRepository repository, StudentRepository studentRepository,
                       TeacherRepository teacherRepository,
                       GradeRepository gradeRepository,
                       RoomRepository roomRepository,
                       ConstraintInstanceRepository constraintInstanceRepository,
                       ConstraintSignatureRepository constraintSignatureRepository,
                       TimeslotRepository timeslotRepository,
                       TimetableRepository timetableRepository, LessonRepository lessonRepository) {
    this.repository = repository;
    this.studentRepository = studentRepository;
    this.teacherRepository = teacherRepository;
    this.gradeRepository = gradeRepository;
    this.roomRepository = roomRepository;
    this.constraintInstanceRepository = constraintInstanceRepository;
    this.constraintSignatureRepository = constraintSignatureRepository;
    this.timeslotRepository = timeslotRepository;
    this.timetableRepository = timetableRepository;
    this.lessonRepository = lessonRepository;
  }

  /**
   * Gets the total count of interesting resources on the server.
   *
   * @return the total count of resources.
   */
  public ServerStatisticsResponseDto getStats() {
    return new ServerStatisticsResponseDto(this.studentRepository.count(),
        this.teacherRepository.count(), this.gradeRepository.count(), this.roomRepository.count(),
        this.constraintSignatureRepository.count());
  }

  /**
   * Gets the current timetable metadata.
   *
   * @return the current timetable metadata.
   */
  public TimetableMetadata getTimetableMetadata() {
    return this.repository.findAll().getFirst().getTimetableMetadata();
  }

  /**
   * Sets the current timetable metadata.
   *
   * @param timetableMetadata the new timetable metadata.
   */
  public void setTimetableMetadata(TimetableMetadata timetableMetadata) {
    Server server = this.repository.findAll().getFirst();
    server.setTimetableMetadata(timetableMetadata);

    this.updateTimeslots(timetableMetadata);

    this.repository.save(server);
  }

  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  private void updateTimeslots(TimetableMetadata timetableMetadata) {
    List<Timeslot> timeslots = new LinkedList<>();

    for (Weekday weekday : Weekday.values()) {
      for (int i = 0; i < timetableMetadata.getTimeslotsAmount(); i++) {
        timeslots.add(new Timeslot(weekday, i, List.of()));
      }
    }

    List<Timeslot> currentSlots = this.timeslotRepository.findAll();

    if (timeslots.size() == currentSlots.size()) {
      return;
    }

    if (timeslots.size() > currentSlots.size()) {
      timeslots.removeIf(slot -> slot.getSlot() < currentSlots.size() / Weekday.values().length);

      this.timeslotRepository.saveAll(timeslots);
      return;
    }

    // currentSlots now contains all the slots that have to be deleted
    currentSlots.removeIf(slot -> slot.getSlot() < timeslots.size() / Weekday.values().length);

    new LessonsDeleter(lessonRepository, timetableRepository)
        .fromTimeSlots(currentSlots);

    List<String> timeslotIds = currentSlots.stream().map(Timeslot::getId).toList();
    new ConstraintInstanceDeleter(constraintSignatureRepository, constraintInstanceRepository)
        .removeAllInstancesWithArgumentValue(timeslotIds.toArray(new String[0]));

    this.timeslotRepository.deleteAll(currentSlots);
  }
}
