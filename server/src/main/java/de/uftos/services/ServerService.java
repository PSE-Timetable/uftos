package de.uftos.services;

import de.uftos.dto.ServerStatisticsResponseDto;
import de.uftos.entities.Server;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerService {
  private final ServerRepository repository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final RoomRepository roomRepository;

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

  public ServerStatisticsResponseDto getStats() {
    return new ServerStatisticsResponseDto(this.studentRepository.count(),
        this.teacherRepository.count(), this.studentGroupRepository.count(),
        this.roomRepository.count(), 0);
  }

  public int getTimeslotLength() {
    return this.repository.findAll().getFirst().getTimeslotLength();
  }

  public void setTimeslotLength(int length) {
    Server server = this.repository.findAll().getFirst();
    server.setTimeslotLength(length);

    this.repository.save(server);
  }
}
