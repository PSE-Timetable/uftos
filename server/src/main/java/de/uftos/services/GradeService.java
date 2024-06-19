package de.uftos.services;

import de.uftos.dto.GradeRequestDto;
import de.uftos.dto.GradeResponseDto;
import de.uftos.dto.LessonResponseDto;
import de.uftos.entities.Grade;
import de.uftos.entities.Student;
import de.uftos.entities.StudentGroup;
import de.uftos.repositories.GradeRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GradeService {
  private final GradeRepository repository;

  @Autowired
  public GradeService(GradeRepository repository) {
    this.repository = repository;
  }

  public Page<GradeResponseDto> get(Pageable pageable) {
    Page<Grade> grades = this.repository.findAll(pageable);
    List<GradeResponseDto> response =
        grades.map(this::mapResponseDto).stream().toList();

    return new PageImpl<>(response, pageable, response.size());
  }

  public GradeResponseDto getById(String id) {
    Grade grade = this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    return this.mapResponseDto(grade);
  }

  public List<LessonResponseDto> getLessonsById(String id) {
    Grade grade = this.repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

    // TODO
    return null;
  }

  public GradeResponseDto create(GradeRequestDto grade) {
    return this.mapResponseDto(this.repository.save(grade.map()));
  }

  public GradeResponseDto update(String id, GradeRequestDto gradeRequest) {
    Grade grade = gradeRequest.map();
    grade.setId(id);

    return this.mapResponseDto(this.repository.save(grade));
  }

  public void delete(String id) {
    var grade = this.repository.findById(id);
    if (grade.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    this.repository.delete(grade.get());
  }

  private GradeResponseDto mapResponseDto(Grade grade) {
    Set<String> studentIds = new HashSet<>();

    for (StudentGroup group : grade.getStudentGroups()) {
      studentIds.addAll(group.getStudents().stream().map(Student::getId).toList());
    }

    return new GradeResponseDto(grade.getId(), grade.getName(),
        grade.getStudentGroups().stream().map(StudentGroup::getId).toList(),
        studentIds.stream().toList(),
        grade.getTags());
  }
}
