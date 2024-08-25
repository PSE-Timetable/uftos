package de.uftos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.search.PostgreSQLTSVectorType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

/**
 * The database entity for grades.
 * Contains an ID, the name of the grade as well as the student groups that are in the
 * grade as well as the tags associated with it.
 */
@Entity(name = "grades")
@Data
@NoArgsConstructor
public class Grade {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;

  @OneToOne(mappedBy = "grade", cascade = CascadeType.REMOVE)
  private Curriculum curriculum;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "grades_student_groups",
      joinColumns = @JoinColumn(name = "grades_id"),
      inverseJoinColumns = @JoinColumn(name = "student_groups_id"))
  private List<StudentGroup> studentGroups;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "grades_tags",
      joinColumns = @JoinColumn(name = "grades_id"),
      inverseJoinColumns = @JoinColumn(name = "tags_id"))
  private List<Tag> tags;

  @JsonIgnore
  @Type(PostgreSQLTSVectorType.class)
  @Column(name = "search_vector", columnDefinition = "tsvector", insertable = false, updatable = false)
  private String searchVector;

  public Grade(String id) {
    this.id = id;
  }

  /**
   * Creates a new grade.
   *
   * @param name            the name of the grade.
   * @param studentGroupIds the IDs of the student groups that are in the grade.
   * @param tagIds          the IDs of the tags associated with the grade.
   */
  public Grade(String name, List<String> studentGroupIds, List<String> tagIds) {
    this.name = name;
    this.studentGroups = studentGroupIds.stream().map(StudentGroup::new).toList();
    this.tags = tagIds.stream().map(Tag::new).toList();
  }


  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    Grade that = (Grade) other;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int initialOddNumber = 17;
    int multiplierOddNumber = 37;
    return new HashCodeBuilder(initialOddNumber, multiplierOddNumber)
        .append(id)
        .append(name)
        .append(tags)
        .toHashCode();
  }
}
