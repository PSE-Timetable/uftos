package de.uftos.services;

import de.uftos.dto.responsedtos.ServerStatisticsResponseDto;
import de.uftos.entities.Server;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
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
  private final GradeRepository gradeRepository;
  private final RoomRepository roomRepository;
  private final ConstraintSignatureRepository constraintSignatureRepository;

  /**
   * Creates a server service.
   *
   * @param repository                    the repository for accessing the server table.
   * @param studentRepository             the repository for accessing the student table.
   * @param teacherRepository             the repository for accessing the teacher table.
   * @param gradeRepository               the repository for accessing the student group table.
   * @param roomRepository                the repository for accessing the room table.
   * @param constraintSignatureRepository the repository for accessing the constraint signature table.
   */
  @Autowired
  public ServerService(ServerRepository repository, StudentRepository studentRepository,
                       TeacherRepository teacherRepository,
                       GradeRepository gradeRepository,
                       RoomRepository roomRepository,
                       ConstraintSignatureRepository constraintSignatureRepository) {
    this.repository = repository;
    this.studentRepository = studentRepository;
    this.teacherRepository = teacherRepository;
    this.gradeRepository = gradeRepository;
    this.roomRepository = roomRepository;
    this.constraintSignatureRepository = constraintSignatureRepository;
  }

  /**
   * Gets the total count of interesting resources on the server.
   *
   * @return the total count of resources.
   */
  public ServerStatisticsResponseDto getStats() {
    return new ServerStatisticsResponseDto(this.studentRepository.count(),
        this.teacherRepository.count(),this.gradeRepository.count(), this.roomRepository.count(),
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

    this.repository.save(server);
  }
}
