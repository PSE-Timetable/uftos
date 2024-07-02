package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The database entity for student groups.
 * Contains an ID, the name of the group and the students in the group, as well as the grades, tags
 * and lessons associated with the student group.
 */
@Entity(name = "student_groups")
@Data
@NoArgsConstructor
public class StudentGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  @ManyToMany
  @JoinTable(name = "students_student_groups",
      joinColumns = @JoinColumn(name = "student_groups_id"),
      inverseJoinColumns = @JoinColumn(name = "students_id"))
  private List<Student> students;

  @ManyToMany(mappedBy = "studentGroups")
  private List<Grade> grades;

  @ManyToMany
  @JoinTable(name = "student_groups_tags",
      joinColumns = @JoinColumn(name = "student_groups_id"),
      inverseJoinColumns = @JoinColumn(name = "tags_id"))
  private List<Tag> tags;

  @JsonIgnore
  @OneToMany(mappedBy = "studentGroup")
  private List<Lesson> lessons;

  /**
   * Creates a new student group.
   * Used if the ID of the student group is known.
   *
   * @param id the ID of the student group.
   */
  public StudentGroup(String id) {
    this.id = id;
  }

  /**
   * Creates a new student group.
   * Used if the ID of the student group isn't known.
   *
   * @param name       the name of the student group.
   * @param studentIds the IDs of the students that are part of the group.
   * @param gradeIds   the IDs of the grade that are part of the student group.
   * @param tagIds     the IDs of the tags associated with the student group.
   */
  public StudentGroup(String name, List<String> studentIds, List<String> gradeIds,
                      List<String> tagIds) {
    this.name = name;
    this.students = studentIds.stream().map(Student::new).toList();
    this.grades = gradeIds.stream().map(Grade::new).toList();
    this.tags = tagIds.stream().map(Tag::new).toList();
  }
}
