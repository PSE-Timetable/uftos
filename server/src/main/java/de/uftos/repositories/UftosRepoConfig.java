package de.uftos.repositories;

import de.uftos.repositories.solver.SolverRepository;
import de.uftos.repositories.solver.SolverRepositoryImpl;
import de.uftos.repositories.ucdl.UcdlRepository;
import de.uftos.repositories.ucdl.UcdlRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A class to initialize the repositories needed by UCDL and the solver.
 */
@Configuration
public class UftosRepoConfig {

  /**
   * Initializes the UCDL repository.
   *
   * @return the UCDL repository.
   */
  @Bean
  public UcdlRepository ucdlRepository() {
    return new UcdlRepositoryImpl();
  }

  /**
   * Initializes the solver repository.
   *
   * @return the solver repository.
   */
  @Bean
  public SolverRepository solverRepository() {
    return new SolverRepositoryImpl();
  }
}
