package de.uftos.repositories;

import de.uftos.dto.StudentAndGroup;
import de.uftos.entities.StudentGroup;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

public interface StudentGroupRepository extends ListPagingAndSortingRepository<StudentGroup, String>, ListCrudRepository<StudentGroup, String> {
    @Query("insert into 'students_studentGroups' ('studentGroupsId', 'studentsId') values :studentAndGroupList")
    void addStudentsToGroups(List<StudentAndGroup> studentAndGroupList);

    @Query("delete from 'students_studentGroups' where studentGroupsId = :groupId and studentsId in :studentIds")
    void removeStudentsFromGroup(String groupId, List<String> studentIds);
}