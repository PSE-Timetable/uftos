package de.uftos;

import de.uftos.entities.Break;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.CurriculumRepository;
import de.uftos.repositories.database.GradeRepository;
import de.uftos.repositories.database.RoomRepository;
import de.uftos.repositories.database.ServerRepository;
import de.uftos.repositories.database.StudentGroupRepository;
import de.uftos.repositories.database.StudentRepository;
import de.uftos.repositories.database.SubjectRepository;
import de.uftos.repositories.database.TagRepository;
import de.uftos.repositories.database.TeacherRepository;
import de.uftos.repositories.database.TimeslotRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Contains the method to start the UFTOS application.
 */
@OpenAPIDefinition(info = @Info(title = "UFTOS OpenAPI definition", version = "v0"), servers = {
    @Server(url = "http://localhost:5173/api", description = "UFTOS api URL")})
@SpringBootApplication
public class UftosApplication {
  /**
   * Starts the UFTOS application.
   *
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(UftosApplication.class, args);
  }

  @Bean
  ApplicationRunner init(ServerRepository serverRepository,
                         GradeRepository gradeRepository,
                         SubjectRepository subjectRepository,
                         RoomRepository roomRepository,
                         StudentGroupRepository studentGroupRepository,
                         StudentRepository studentRepository,
                         TeacherRepository teacherRepository,
                         CurriculumRepository curriculumRepository,
                         TagRepository tagRepository,
                         TimeslotRepository timeslotRepository
  ) {
    return (ApplicationArguments args) -> {
      System.out.println("clearing all relations");
      serverRepository.deleteAll();
      gradeRepository.deleteAll();
      teacherRepository.deleteAll();
      subjectRepository.deleteAll();
      roomRepository.deleteAll();
      studentGroupRepository.deleteAll();
      studentRepository.deleteAll();
      curriculumRepository.deleteAll();
      tagRepository.deleteAll();
      timeslotRepository.deleteAll();
      System.out.println("cleared all relations");

      FillDB.fillDB(
          gradeRepository,
          subjectRepository,
          roomRepository,
          studentGroupRepository,
          studentRepository,
          teacherRepository,
          curriculumRepository,
          tagRepository,
          timeslotRepository
      );
      serverRepository.save(
          new de.uftos.entities.Server(new TimetableMetadata(45, "7:45", new Break[] {}), "2024"));
    };
  }
}
