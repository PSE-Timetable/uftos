package de.uftos.services;

import de.uftos.dto.requestdtos.TimeslotRequestDto;
import de.uftos.entities.Timeslot;
import de.uftos.repositories.database.ConstraintInstanceRepository;
import de.uftos.repositories.database.ConstraintSignatureRepository;
import de.uftos.repositories.database.TimeslotRepository;
import de.uftos.utils.ConstraintInstanceDeleter;
import de.uftos.utils.SpecificationBuilder;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The service providing the logic of the /timeslots endpoint.
 */
@Service
public class TimeslotService {
  private final TimeslotRepository repository;
  private final ConstraintSignatureRepository constraintSignatureRepository;
  private final ConstraintInstanceRepository constraintInstanceRepository;

  /**
   * Creates a timeslot service.
   *
   * @param repository                    the repository for accessing the timeslot table.
   * @param constraintSignatureRepository the repository for accessing the constraint signature table.
   * @param constraintInstanceRepository  the repository for accessing the constraint instance table.
   */
  @Autowired
  public TimeslotService(TimeslotRepository repository,
                         ConstraintSignatureRepository constraintSignatureRepository,
                         ConstraintInstanceRepository constraintInstanceRepository) {
    this.repository = repository;
    this.constraintSignatureRepository = constraintSignatureRepository;
    this.constraintInstanceRepository = constraintInstanceRepository;
  }

  /**
   * Gets a list of entries of the timeslot table.
   *
   * @param tags the tags filter.
   * @return the list of entries fitting the parameters.
   */
  public List<Timeslot> get(Optional<String[]> tags) {
    Specification<Timeslot> spec = new SpecificationBuilder<Timeslot>()
        .optionalAndJoinIn(tags, "tags", "id")
        .build();

    return this.repository.findAll(spec);
  }

  /**
   * Gets a timeslot from their ID.
   *
   * @param id the ID of the timeslot.
   * @return the timeslot with the given ID.
   * @throws ResponseStatusException is thrown if the ID doesn't have a corresponding timeslot.
   */
  public Timeslot getById(String id) {
    Optional<Timeslot> timeslot = this.repository.findById(id);

    return timeslot.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Could not find a timeslot with this id"));
  }

  /**
   * Creates a new timeslot in the database.
   *
   * @param timeslot the information about the timeslot which is to be created.
   * @return the created timeslot which includes the ID that was assigned.
   * @throws ResponseStatusException is thrown if the ID defined in the timeslot parameter is
   *                                 already present in the database.
   */
  public Timeslot create(TimeslotRequestDto timeslot) {
    return this.repository.save(timeslot.map());
  }

  /**
   * Updates the timeslot with the given ID.
   *
   * @param id              the ID of the timeslot which is to be updated.
   * @param timeslotRequest the updated timeslot information.
   * @return the updated timeslot.
   */
  public Timeslot update(String id, TimeslotRequestDto timeslotRequest) {
    Timeslot timeslot = timeslotRequest.map();
    timeslot.setId(id);

    return this.repository.save(timeslot);
  }

  /**
   * Deletes the timeslot with the given ID.
   *
   * @param id the ID of the timeslot which is to be deleted.
   * @throws ResponseStatusException is thrown if no timeslot exists with the given ID.
   */
  public void delete(String id) {
    Optional<Timeslot> timeslot = this.repository.findById(id);
    if (timeslot.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Could not find a timeslot with this id");
    }

    new ConstraintInstanceDeleter(constraintSignatureRepository, constraintInstanceRepository)
        .removeAllInstancesWithArgumentValue(new String[] {id});

    this.repository.delete(timeslot.get());
  }
}
