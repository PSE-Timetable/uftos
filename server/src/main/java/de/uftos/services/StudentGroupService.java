package de.uftos.services;

import de.uftos.dto.StudentAndGroup;
import de.uftos.dto.StudentGroupRequestDto;
import de.uftos.entities.StudentGroup;
import de.uftos.repositories.StudentGroupRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentGroupService {
  private final StudentGroupRepository repository;

  @Autowired
  public StudentGroupService(StudentGroupRepository repository) {
    this.repository = repository;
  }

  public Page<StudentGroup> get(Pageable pageable) {
    return this.repository.findAll(pageable);
  }

  public StudentGroup getById(String id) {
    var group = this.repository.findById(id);

    return group.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
  }

  public StudentGroup create(StudentGroupRequestDto group) {
    return this.repository.save(group.map());
  }

  public StudentGroup update(String id, StudentGroupRequestDto groupRequest) {
    StudentGroup group = groupRequest.map();
    group.setId(id);

    return this.repository.save(group);
  }

  public StudentGroup addStudents(String id, List<String> studentIds) {
    this.repository.addStudentsToGroups(
        studentIds.stream().map((studentId) -> new StudentAndGroup(studentId, id)).toList());
    return this.getById(id);
  }

  public void removeStudents(String id, List<String> studentIds) {
    this.repository.removeStudentsFromGroup(id, studentIds);
  }

  public void delete(String id) {
    var group = this.repository.findById(id);
    if (group.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(group.get());
  }
}
