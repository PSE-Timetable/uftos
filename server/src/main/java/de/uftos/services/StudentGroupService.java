package de.uftos.services;

import de.uftos.dto.StudentAndGroup;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.repositories.StudentGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    public StudentGroup create(StudentGroup group) {
        if (group.getId() != null && this.repository.findById(group.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return this.repository.save(group);
    }

    public StudentGroup update(String id, StudentGroup group) {
        group.setId(id);

        return this.repository.save(group);
    }

    public void addStudents(String id, List<Student> students) {
        this.repository.addStudentsToGroups(students.stream().map((student) -> new StudentAndGroup(student.getId(), id)).toList());
    }

    public void removeStudents(String id, List<Student> students) {
        this.repository.removeStudentsFromGroup(id, students.stream().map(Student::getId).toList());
    }

    public void delete(String id) {
        var group = this.repository.findById(id);
        if (group.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        this.repository.delete(group.get());
    }
}
