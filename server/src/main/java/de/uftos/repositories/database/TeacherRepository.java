package de.uftos.repositories.database;

import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import de.uftos.entities.Teacher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the teacher database table.
 */
public interface TeacherRepository extends ListPagingAndSortingRepository<Teacher, String>,
    ListCrudRepository<Teacher, String>, JpaSpecificationExecutor<Teacher> {

  List<Teacher> findByTags(Tag tag);

  @Query("SELECT te FROM teachers te JOIN te.tags t WHERE t IN :tagIds")
  List<Teacher> findAllByTags(List<String> tagIds);

  List<Teacher> findBySubjects(Subject subject);
}
