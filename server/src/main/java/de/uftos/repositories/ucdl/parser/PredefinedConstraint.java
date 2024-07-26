package de.uftos.repositories.ucdl.parser;

import de.uftos.dto.ResourceType;
import java.util.List;
import lombok.Getter;

/**
 * This enum models predefined constraints which are ready-to-use.
 */
@Getter
public enum PredefinedConstraint {
  TEACHER_COLLISION("teacherCollision",
      """
          teacherCollision:
            description: "Teachers may not have multiple lessons at the same time."
            default_type: HARD_PENALIZE
            parameter:
              lesson1: Lesson
              lesson2: Lesson
            definition: >-
              lesson1 != lesson2 && lesson1.timeslot == lesson2.timeslot && lesson1.teacher == lesson2.teacher
          """,
      List.of(ResourceType.LESSON, ResourceType.LESSON)
  ),
  STUDENT_COLLISION("studentCollision",
      """
          studentCollision:
            description: "Students may not have multiple lessons at the same time."
            default_type: HARD_PENALIZE
            parameter:
              lesson1: Lesson
              lesson2: Lesson
            definition: >-
              lesson1 != lesson2
              && lesson1.timeslot == lesson2.timeslot
              && size(lesson1.studentGroup.students[lesson2.studentGroup.students]) > 0
          """,
      List.of(ResourceType.LESSON, ResourceType.LESSON)
  ),
  ROOM_COLLISION("roomCollision",
      """
          roomCollision:
            description: "Rooms may not be occupied by multiple lessons at the same time."
            default_type: HARD_PENALIZE
            parameter:
              lesson1: Lesson
              lesson2: Lesson
            definition: >-
              lesson1 != lesson2 && lesson1.timeslot == lesson2.timeslot && lesson1.room == lesson2.room
          """,
      List.of(ResourceType.LESSON, ResourceType.LESSON)
  ),
  WORKING_HOURS("workingHours",
      """
          workingHours:
            description: "Teacher {teacher} does work during Timeslot {timeslot}."
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
            description: "Teacher {teacher} teaches student group {group} in subject {subject}."
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
            description: "Subject {subject} is only taught in rooms with tag {tag}."
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
