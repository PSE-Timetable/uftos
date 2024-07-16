package de.uftos;

import de.uftos.entities.Break;
import de.uftos.entities.TimetableMetadata;
import de.uftos.repositories.database.ServerRepository;
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
  ApplicationRunner init(ServerRepository serverRepository) {
    return (ApplicationArguments args) -> serverRepository.save(
        new de.uftos.entities.Server(new TimetableMetadata(45, "7:45", new Break[] {}), "2024"));
  }
}
