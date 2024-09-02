package de.uftos.repositories.database;

import de.uftos.entities.Tag;
import de.uftos.entities.Timeslot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the timeslot database table.
 */
public interface TimeslotRepository extends ListPagingAndSortingRepository<Timeslot, String>,
    ListCrudRepository<Timeslot, String>, JpaSpecificationExecutor<Timeslot> {

  List<Timeslot> findByTags(Tag tag);

  @Query("SELECT ti FROM timeslots ti JOIN ti.tags t WHERE t IN :tagIds")
  List<Timeslot> findAllByTags(List<String> tagIds);
}
