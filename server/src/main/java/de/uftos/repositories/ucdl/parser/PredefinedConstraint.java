package de.uftos.repositories.ucdl.parser;

import lombok.Getter;

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
                    
          """),
  STUDENT_COLLISION("studentCollision",
      """
          studentCollision:
            description: "Students may not have multiple lessons at the same time."
            default_type: HARD_PENALIZE
            parameter:
              lesson1: Lesson
              lesson2: Lesson
            definition: >-
              lesson1 != lesson2 && lesson1.timeslot == lesson2.timeslot && size(lesson1.studentGroup.students[lesson2.studentGroup.students]) > 0
                    
          """
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
                    
          """
  ),
  WORKING_HOURS("workingHours",
      "workingHours:\n"
          + "  description: \"Teacher {teacher} does work during Timeslot {timeslot}.\"\n"
          + "  default_type: SOFT_PENALIZE\n"
          + "  parameter:\n"
          + "    teacher: Teacher\n"
          + "    timeslot: Timeslot\n"
          + "  definition: >-\n"
          + "    if (timeslot in teacher.lessons.timeslot) {\n"
          + "      return true\n"
          + "    }\n"
          + "    return false\n"),
  TEACHER_TEACHES_GROUP("teacherTeachesGroup",
      "teacherTeachesGroup:\n"
          +
          "  description: \"Teacher {teacher} teaches student group {group} in subject {subject}.\"\n"
          + "  default_type: SOFT_PENALIZE\n"
          + "  parameter:\n"
          + "    teacher: Teacher\n"
          + "    group: Student-Group\n"
          + "    subject: Subject\n"
          + "  definition: >-\n"
          + "    forall (lesson : group.lessons[this.subject == subject]) {\n"
          + "      lesson.teacher == teacher\n"
          + "    }\n"),
  SUBJECT_ROOM("subjectRoom",
      "subjectRoom:\n"
          + "  description: \"Subject {subject} is only taught in rooms with tag {tag}.\"\n"
          + "  default_type: SOFT_PENALIZE\n"
          + "  parameter:\n"
          + "    subject: Subject\n"
          + "    tag: Tag\n"
          + "  definition: >-\n"
          + "    forall (room : subject.lessons.room) {\n"
          + "      tag in room.tags\n"
          + "    }\n");

  private final String name;
  private final String code;

  PredefinedConstraint(String name, String code) {
    this.name = name;
    this.code = code;
  }
}
