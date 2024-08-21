package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
  @NotEmpty
  private String id;

  @NotEmpty
  private String name;

  @NotNull
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "students_student_groups",
      joinColumns = @JoinColumn(name = "student_groups_id"),
      inverseJoinColumns = @JoinColumn(name = "students_id"))
  private List<Student> students;

  @NotNull
  @ManyToMany(mappedBy = "studentGroups")
  private List<Grade> grades;

  @NotNull
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "student_groups_tags",
      joinColumns = @JoinColumn(name = "student_groups_id"),
      inverseJoinColumns = @JoinColumn(name = "tags_id"))
  private List<Tag> tags;

  @JsonIgnore
  @OneToMany(mappedBy = "studentGroup")
  private List<Lesson> lessons;

  @NotNull
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "student_groups_subjects",
      joinColumns = @JoinColumn(name = "student_groups_id"),
      inverseJoinColumns = @JoinColumn(name = "subjects_id"))
  private List<Subject> subjects;

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
   * @param name        the name of the student group.
   * @param studentIds  the IDs of the students that are part of the group.
   * @param tagIds      the IDs of the tags associated with the student group.
   * @param subjectsIds the IDs of the subjects associated with the student group.
   */
  public StudentGroup(String name, List<String> studentIds,
                      List<String> tagIds, List<String> subjectsIds) {
    this.name = name;
    this.students = studentIds.stream().map(Student::new).toList();
    this.tags = tagIds.stream().map(Tag::new).toList();
    this.subjects = subjectsIds.stream().map(Subject::new).toList();
  }




  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    StudentGroup that = (StudentGroup) other;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int initialOddNumber = 23;
    int multiplierOddNumber = 59;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(name)
        .append(subjects)
        .append(tags)
        .toHashCode();
  }

}
