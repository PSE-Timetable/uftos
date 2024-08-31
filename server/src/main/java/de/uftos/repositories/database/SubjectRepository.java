package de.uftos.repositories.database;

import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

/**
 * The repository for accessing the subject database table.
 */
public interface SubjectRepository
    extends ListCrudRepository<Subject, String>, JpaSpecificationExecutor<Subject> {

  List<Subject> findByTags(Tag tag);

  @Query("SELECT s FROM subjects s JOIN s.tags t WHERE t IN :tagIds")
  List<Subject> findAllByTags(List<String> tagIds);
}
