package de.uftos.dto.solver;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import java.util.ArrayList;
import java.util.List;

/**
 * The data transfer object of a timetable for communication with solvers.
 */
public record TimetableProblemDto(
    List<GradeProblemDto> grades,
    List<LessonProblemDto> lessons,
    List<RoomProblemDto> rooms,
    List<StudentGroupProblemDto> studentGroups,
    List<StudentProblemDto> students,
    List<SubjectProblemDto> subjects,
    List<TagProblemDto> tags,
    List<TeacherProblemDto> teachers,
    List<TimeslotProblemDto> timeslots,
    List<ConstraintDefinitionDto> definitions,
    List<ConstraintInstanceDto> instances
) implements ResourceProblemDto {
  /**
   * Gets a List of all resources contained in this timetable.
   *
   * @return a List of all resources contained by this timetable.
   */
  public List<ResourceProblemDto> getResources() {
    List<ResourceProblemDto> resources = new ArrayList<>();

    resources.addAll(grades);
    resources.addAll(lessons);
    resources.addAll(rooms);
    resources.addAll(studentGroups);
    resources.addAll(students);
    resources.addAll(subjects);
    resources.addAll(tags);
    resources.addAll(teachers);
    resources.addAll(timeslots);

    return resources;
  }

  @Override
  public String getId() {
    return "this";
  }

  @Override
  public ResourceType getType() {
    return ResourceType.TIMETABLE;
  }
}
