package de.uftos.repositories.solver;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
  void solveValid() {
    assertDoesNotThrow(() -> {
      new SolverRepositoryImpl().solve(getValidTimetableProblemDto(), (timetable) -> {
      }).get();
    });

    TimetableProblemDto noConstraintInstances = getValidTimetableProblemDto();
    noConstraintInstances.instances().clear();
    assertDoesNotThrow(() -> {
      new SolverRepositoryImpl().solve(noConstraintInstances, (timetable) -> {
      }).get();
    });

    TimetableProblemDto lessonsUnset = getValidTimetableProblemDto();
    lessonsUnset.lessons().clear();
    lessonsUnset.lessons()
        .add(new LessonProblemDto(null, 0, null, "studentGroup", null, "subject", null));

    RoomProblemDto oldRoom = lessonsUnset.rooms().getFirst();
    lessonsUnset.rooms()
        .set(0, new RoomProblemDto(oldRoom.id(), oldRoom.tagIds(), new ArrayList<>()));

    TeacherProblemDto oldTeacher = lessonsUnset.teachers().getFirst();
    lessonsUnset.teachers().set(0,
        new TeacherProblemDto(oldTeacher.id(), oldTeacher.tagIds(), new ArrayList<>(),
            oldTeacher.subjectIds()));

    TimeslotProblemDto oldTimeslot = lessonsUnset.timeslots().getFirst();
    lessonsUnset.timeslots().set(0,
        new TimeslotProblemDto(oldTimeslot.id(), oldTimeslot.day(), oldTimeslot.slot(),
            oldTimeslot.tagIds(), new ArrayList<>()));

    assertDoesNotThrow(() -> {
      new SolverRepositoryImpl().solve(lessonsUnset, (timetable) -> {
      });
    });
  }

  @Test
  void solveInvalid() {
    //Exceptions are thrown in the solver thread and therefore the .solve() method does not throw by itself
    TimetableProblemDto timetable;
    TimetableProblemDto invalidTimetable;

    timetable = getValidTimetableProblemDto();
    invalidTimetable =
        new TimetableProblemDto(null, timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), null, timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable1 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable1, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), null,
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable2 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable2, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            null, timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable3 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable3, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), null, timetable.subjects(), timetable.tags(),
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable4 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable4, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), null, timetable.tags(),
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable5 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable5, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), null,
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable6 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable6, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            null, timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable7 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable7, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), null, timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable8 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable8, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), timetable.timeslots(), null,
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable9 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable9, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            null);
    TimetableProblemDto finalInvalidTimetable10 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable10, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), new ArrayList<>(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable11 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable11, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            new ArrayList<>(), timetable.timeslots(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable12 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable12, (t) -> {
    }));
    invalidTimetable =
        new TimetableProblemDto(timetable.grades(), timetable.lessons(), timetable.rooms(),
            timetable.studentGroups(), timetable.students(), timetable.subjects(), timetable.tags(),
            timetable.teachers(), new ArrayList<>(), timetable.definitions(),
            timetable.instances());
    TimetableProblemDto finalInvalidTimetable13 = invalidTimetable;
    assertDoesNotThrow(() -> new SolverRepositoryImpl().solve(finalInvalidTimetable13, (t) -> {
    }));
  }

  private TimetableProblemDto getValidTimetableProblemDto() {
    GradeProblemDto grade = new GradeProblemDto("grade", new ArrayList<>(List.of("tag")),
        new ArrayList<>(List.of("studentGroup")));
    RoomProblemDto room = new RoomProblemDto("room", new ArrayList<>(List.of("tag")),
        new ArrayList<>(List.of("lesson")));
    StudentProblemDto student =
        new StudentProblemDto("student", new ArrayList<>(List.of("tag")),
            new ArrayList<>(List.of("studentGroup")));
    StudentGroupProblemDto studentGroup =
        new StudentGroupProblemDto("studentGroup", "grade", new ArrayList<>(List.of("tag")),
            new ArrayList<>(List.of("lesson")),
            new ArrayList<>(List.of("student")));
    SubjectProblemDto subject =
        new SubjectProblemDto("subject", new ArrayList<>(List.of("tag")),
            new ArrayList<>(List.of("lesson")), new ArrayList<>(List.of("teacher")));
    TagProblemDto tag =
        new TagProblemDto("tag", new ArrayList<>(List.of("grade")),
            new ArrayList<>(List.of("room")), new ArrayList<>(List.of("student")),
            new ArrayList<>(List.of("studentGroup")), new ArrayList<>(List.of("subject")),
            new ArrayList<>(List.of("teacher")),
            new ArrayList<>(List.of("timeslot")));
    TeacherProblemDto teacher =
        new TeacherProblemDto("teacher", new ArrayList<>(List.of("tag")),
            new ArrayList<>(List.of("lesson")), new ArrayList<>(List.of("subject")));
    TimeslotProblemDto timeslot =
        new TimeslotProblemDto("timeslot", 0, 0, new ArrayList<>(List.of("tag")),
            new ArrayList<>(List.of("lesson")));
    LessonProblemDto lesson =
        new LessonProblemDto("lesson", 0, "teacher", "studentGroup",
            "timeslot", "subject", "room");

    List<ResourceType> parameterTypes =
        new ArrayList<>(List.of(ResourceType.GRADE, ResourceType.ROOM, ResourceType.STUDENT,
            ResourceType.STUDENT_GROUP, ResourceType.SUBJECT, ResourceType.TAG,
            ResourceType.TEACHER, ResourceType.TIMESLOT));

    LinkedHashMap<String, ResourceType> definitionParameters = new LinkedHashMap<>();
    for (ResourceType resourceType : parameterTypes) {
      definitionParameters.put(resourceType.name(), resourceType);
    }
    ConstraintDefinitionDto constraintDefinition = new ConstraintDefinitionDto("testConstraint", "",
        RewardPenalize.HARD_PENALIZE, definitionParameters,
        new ValueDto<>(UcdlToken.BOOL_VALUE, true));
    ConstraintInstanceDto constraintInstance =
        new ConstraintInstanceDto("testConstraint", RewardPenalize.SOFT_REWARD,
            new ArrayList<>(
                List.of(grade, room, student, studentGroup, subject, tag, teacher, timeslot)));

    return new TimetableProblemDto(new ArrayList<>(List.of(grade)),
        new ArrayList<>(List.of(lesson)),
        new ArrayList<>(List.of(room)), new ArrayList<>(List.of(studentGroup)),
        new ArrayList<>(List.of(student)), new ArrayList<>(List.of(subject)),
        new ArrayList<>(List.of(tag)),
        new ArrayList<>(List.of(teacher)), new ArrayList<>(List.of(timeslot)),
        new ArrayList<>(List.of(constraintDefinition)),
        new ArrayList<>(List.of(constraintInstance)));
  }
}