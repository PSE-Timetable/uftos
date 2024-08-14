package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.ResourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;


/**
 * This enum models predefined constraints which are ready-to-use.
 */
@SuppressWarnings("checkstyle:LineLength")
@Getter
public enum PredefinedConstraint {
  EMPTY_CONSTRAINT("newConstraint",
      """
          newConstraint:
            description: "Ein neuer Constraint, der immer zu false auswertet."
            default_type: HARD_PENALIZE
            parameter:
            definition: >-
              if (false) {
                return true
              }
              return false
          """,
      new ArrayList<>()
  ),

  TEACHER_COLLISION("teacherCollision",
      """
          teacherCollision:
            description: "Lehrer {teacher} kann nicht mehrere Unterrichtseinheiten zeitgleich unterrichten."
            default_type: HARD_PENALIZE
            parameter:
              teacher: Teacher
            definition: >-
              exists (lesson1 : teacher.lessons) {
                exists (lesson2 : teacher.lessons) {
                  lesson1 != lesson2 && lesson1.timeslot == lesson2.timeslot && lesson1.teacher == lesson2.teacher
                }
              }
          """,
      List.of(ResourceType.TEACHER)
  ),
  STUDENT_COLLISION("studentCollision",
      """
          studentCollision:
            description: "Schüler {student} kann nicht an mehreren Unterrichtseinheiten zeitgleich teilnehmen."
            default_type: HARD_PENALIZE
            parameter:
              student: Student
            definition: >-
              exists (lesson1 : student.studentGroups.lessons) {
                exists (lesson2 : student.studentGroups.lessons) {
                  lesson1 != lesson2 && lesson1.timeslot == lesson2.timeslot && lesson1.teacher == lesson2.teacher
                }
              }
          """,
      List.of(ResourceType.STUDENT)
  ),
  ROOM_COLLISION("roomCollision",
      """
          roomCollision:
            description: "In Raum {room} können nicht mehrere Unterrichtseinheiten zeitgleich unterrichtet werden."
            default_type: HARD_PENALIZE
            parameter:
              room: Room
            definition: >-
              exists (lesson1 : room.lessons) {
                exists (lesson2 : room.lessons) {
                  lesson1 != lesson2 && lesson1.timeslot == lesson2.timeslot && lesson1.teacher == lesson2.teacher
                }
              }
          """,
      List.of(ResourceType.ROOM)
  ),
  WORKING_HOURS("workingHours",
      """
          workingHours:
            description: "Lehrer {teacher} arbeitet zum Zeitpunkt {timeslot}."
            default_type: SOFT_PENALIZE
            parameter:
              teacher: Teacher
              timeslot: Timeslot
            definition: >-
              if (timeslot in teacher.lessons.timeslot) {
                return true
              }
              return false
          """,
      List.of(ResourceType.TEACHER, ResourceType.TIMESLOT)
  ),
  TEACHER_TEACHES_GROUP("teacherTeachesGroup",
      """
          teacherTeachesGroup:
            description: "Lehrer {teacher} unterrichtet Schülergruppe {group} in Fach {subject}."
            default_type: SOFT_PENALIZE
            parameter:
              teacher: Teacher
              group: Student-Group
              subject: Subject
            definition: >-
              forall (lesson : group.lessons[this.subject == subject]) {
                lesson.teacher == teacher
              }
          """,
      List.of(ResourceType.TEACHER, ResourceType.STUDENT_GROUP, ResourceType.SUBJECT)
  ),
  SUBJECT_ROOM("subjectRoom",
      """
          subjectRoom:
            description: "Fach {subject} kann nur in Räumen mit Tag {tag} unterrichtet werden."
            default_type: SOFT_PENALIZE
            parameter:
              subject: Subject
              tag: Tag
            definition: >-
              forall (room : subject.lessons.room) {
                tag in room.tags
              }
          """,
      List.of(ResourceType.SUBJECT, ResourceType.ROOM)
  );

  private final String name;
  private final String code;
  private final List<ResourceType> parameters;

  PredefinedConstraint(String name, String code, List<ResourceType> parameters) {
    this.name = name;
    this.code = code;
    this.parameters = parameters;
  }
}
