package de.uftos.services;

import de.uftos.dto.responsedtos.ServerStatisticsResponseDto;
import de.uftos.entities.Server;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.TeacherRepository;
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
  private final StudentGroupRepository studentGroupRepository;
  private final RoomRepository roomRepository;

  /**
   * Creates a server service.
   *
   * @param repository             the repository for accessing the server table.
   * @param studentRepository      the repository for accessing the student table.
   * @param teacherRepository      the repository for accessing the teacher table.
   * @param studentGroupRepository the repository for accessing the student group table.
   * @param roomRepository         the repository for accessing the room table.
   */
  @Autowired
  public ServerService(ServerRepository repository, StudentRepository studentRepository,
                       TeacherRepository teacherRepository,
                       StudentGroupRepository studentGroupRepository,
                       RoomRepository roomRepository) {
    this.repository = repository;
    this.studentRepository = studentRepository;
    this.teacherRepository = teacherRepository;
    this.studentGroupRepository = studentGroupRepository;
    this.roomRepository = roomRepository;
  }

  /**
   * Gets the total count of interesting resources on the server.
   *
   * @return the total count of resources.
   */
  public ServerStatisticsResponseDto getStats() {
    return new ServerStatisticsResponseDto(this.studentRepository.count(),
        this.teacherRepository.count(), this.studentGroupRepository.count(),
        this.roomRepository.count(), 0);
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

    this.repository.save(server);
  }
}
