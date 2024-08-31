package de.uftos.repositories.database;

import de.uftos.entities.Grade;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the grade database table.
 */
public interface GradeRepository extends ListPagingAndSortingRepository<Grade, String>,
    ListCrudRepository<Grade, String>, JpaSpecificationExecutor<Grade> {

  List<Grade> findByTags(Tag tag);

  @Query("SELECT g FROM grades g JOIN g.tags t WHERE t IN :tagIds")
  List<Grade> findAllByTags(List<String> tagIds);

  List<Grade> findByStudentGroups(StudentGroup studentGroup);

}
