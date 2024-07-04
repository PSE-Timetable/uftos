package de.uftos;

import de.uftos.entities.Server;
import de.uftos.repositories.database.ServerRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Contains the method to start the UFTOS application.
 */
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
  ApplicationRunner init(ServerRepository serverRepository) {
    return (ApplicationArguments args) -> serverRepository.save(new Server(45, "2024"));
  }
}
