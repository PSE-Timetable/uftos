package de.uftos.repositories.database;

import de.uftos.dto.StudentAndGroup;
import de.uftos.entities.StudentGroup;
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
  /**
   * Adds students to student groups.
   *
   * @param studentAndGroupList list of data transfer objects defining a student and the student
   *                            group to which it is to be added.
   */
  @Query(value = "insert into 'students_studentGroups' ('studentGroupsId', 'studentsId') values"
      + " :studentAndGroupList", nativeQuery = true)
  void addStudentsToGroups(List<StudentAndGroup> studentAndGroupList);

  /**
   * Removes students from student group.
   *
   * @param groupId    the ID of the group from which the students are to be removed.
   * @param studentIds the IDs of the students which are to be removed from the group.
   */
  @Query(value = "delete from 'students_studentGroups' where studentGroupsId = :groupId and "
      + "studentsId in :studentIds", nativeQuery = true)
  void removeStudentsFromGroup(String groupId, List<String> studentIds);
}