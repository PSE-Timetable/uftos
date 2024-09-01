package de.uftos.repositories.solver.timefold;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import de.uftos.dto.ResourceType;
import de.uftos.dto.solver.ConstraintInstanceDto;
import de.uftos.dto.solver.GradeProblemDto;
import de.uftos.dto.solver.LessonProblemDto;
import de.uftos.dto.solver.RewardPenalize;
import de.uftos.dto.solver.RoomProblemDto;
import de.uftos.dto.solver.StudentGroupProblemDto;
import de.uftos.dto.solver.StudentProblemDto;
import de.uftos.dto.solver.SubjectProblemDto;
import de.uftos.dto.solver.TagProblemDto;
import de.uftos.dto.solver.TeacherProblemDto;
import de.uftos.dto.solver.TimeslotProblemDto;
import de.uftos.dto.solver.TimetableProblemDto;
import de.uftos.dto.ucdl.ConstraintDefinitionDto;
import de.uftos.dto.ucdl.UcdlToken;
import de.uftos.dto.ucdl.ast.ValueDto;
import de.uftos.repositories.solver.SolverRepositoryImpl;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("checkstyle:all")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SolverRepositoryImplTest {

  @Test
  void solveValid() throws ExecutionException, InterruptedException {
    assertDoesNotThrow(() -> {
      new SolverRepositoryImpl().solve(getValidTimetableProblemDto(), (timetable) -> {
      });
    });
    System.out.println(
        new SolverRepositoryImpl().solve(getValidTimetableProblemDto(), (timetable) -> {
        }).get());
  }

  private TimetableProblemDto getValidTimetableProblemDto() {
    GradeProblemDto grade = new GradeProblemDto("grade", List.of("tag"), List.of("studentGroup"));
    RoomProblemDto room = new RoomProblemDto("room", List.of("tag"), List.of("lesson"));
    StudentProblemDto student =
        new StudentProblemDto("student", List.of("tag"), List.of("studentGroup"));
    StudentGroupProblemDto studentGroup =
        new StudentGroupProblemDto("studentGroup", "grade", List.of("tag"), List.of("lesson"),
            List.of("student"));
    SubjectProblemDto subject =
        new SubjectProblemDto("subject", List.of("tag"), List.of("lesson"), List.of("teacher"));
    TagProblemDto tag =
        new TagProblemDto("tag", List.of("grade"), List.of("room"), List.of("student"),
            List.of("studentGroup"), List.of("subject"), List.of("teacher"),
            List.of("timeslot"));
    TeacherProblemDto teacher =
        new TeacherProblemDto("teacher", List.of("tag"), List.of("lesson"), List.of("subject"));
    TimeslotProblemDto timeslot =
        new TimeslotProblemDto("timeslot", 0, 0, List.of("tag"), List.of("lesson"));
    LessonProblemDto lesson =
        new LessonProblemDto("lesson", 0, "teacher", "studentGroup",
            "timeslot", "subject", "room");

    List<ResourceType> parameterTypes =
        List.of(ResourceType.GRADE, ResourceType.ROOM, ResourceType.STUDENT,
            ResourceType.STUDENT_GROUP, ResourceType.SUBJECT, ResourceType.TAG,
            ResourceType.TEACHER, ResourceType.TIMESLOT);

    LinkedHashMap<String, ResourceType> definitionParameters = new LinkedHashMap<>();
    for (ResourceType resourceType : parameterTypes) {
      definitionParameters.put(resourceType.name(), resourceType);
    }
    ConstraintDefinitionDto constraintDefinition = new ConstraintDefinitionDto("testConstraint", "",
        RewardPenalize.HARD_PENALIZE, definitionParameters,
        new ValueDto<>(UcdlToken.BOOL_VALUE, true));
    ConstraintInstanceDto constraintInstance =
        new ConstraintInstanceDto("testConstraint", RewardPenalize.SOFT_REWARD,
            List.of(grade, room, student, studentGroup, subject, tag, teacher, timeslot));

    return new TimetableProblemDto(List.of(grade), List.of(lesson),
        List.of(room), List.of(studentGroup), List.of(student), List.of(subject), List.of(tag),
        List.of(teacher), List.of(timeslot), List.of(constraintDefinition),
        List.of(constraintInstance));
  }
}