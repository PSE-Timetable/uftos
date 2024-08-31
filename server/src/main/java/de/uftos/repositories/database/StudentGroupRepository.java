package de.uftos.repositories.database;

import de.uftos.entities.Grade;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.entities.Subject;
import de.uftos.entities.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;


/**
 * The repository for accessing the student group database table.
 */
public interface StudentGroupRepository
    extends ListPagingAndSortingRepository<StudentGroup, String>,
    ListCrudRepository<StudentGroup, String>, JpaSpecificationExecutor<StudentGroup> {

  List<StudentGroup> findByStudents(Student student);

  List<StudentGroup> findByTags(Tag tag);

  @Query("SELECT sg FROM student_groups sg JOIN sg.tags t WHERE t IN :tagIds")
  List<StudentGroup> findAllByTags(List<String> tagIds);

  List<StudentGroup> findBySubjects(Subject subject);

  List<StudentGroup> findByGrades(Grade grade);

  @Query("SELECT sg FROM student_groups sg LEFT JOIN sg.grades g WHERE g.id IN :gradeIds")
  List<StudentGroup> findAllByGrades(List<String> gradeIds);

}
