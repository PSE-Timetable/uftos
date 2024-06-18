package de.uftos.repositories;

import de.uftos.dto.StudentAndGroup;
import de.uftos.entities.StudentGroup;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface StudentGroupRepository
    extends ListPagingAndSortingRepository<StudentGroup, String>,
    ListCrudRepository<StudentGroup, String> {
  @Query(value = "insert into 'students_studentGroups' ('studentGroupsId', 'studentsId') values :studentAndGroupList", nativeQuery = true)
  void addStudentsToGroups(List<StudentAndGroup> studentAndGroupList);

  @Query(value = "delete from 'students_studentGroups' where studentGroupsId = :groupId and studentsId in :studentIds", nativeQuery = true)
  void removeStudentsFromGroup(String groupId, List<String> studentIds);
}