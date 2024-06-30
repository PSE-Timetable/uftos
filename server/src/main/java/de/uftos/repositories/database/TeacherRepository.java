package de.uftos.repositories.database;

import de.uftos.entities.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.lang.NonNull;

/**
 * The repository for accessing the teacher database table.
 */
public interface TeacherRepository extends ListPagingAndSortingRepository<Teacher, String>,
    ListCrudRepository<Teacher, String>, JpaSpecificationExecutor<Teacher> {

  @NonNull
  Page<Teacher> findAll(@NonNull Specification<Teacher> spec, @NonNull Pageable pageable);

}
