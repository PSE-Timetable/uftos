package de.uftos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

}
