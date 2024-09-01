import { goto } from '$app/navigation';
import { Day, getTimeslots, type LessonResponseDto } from '$lib/sdk/fetch-client';
import type { Slot } from '../lesson/lesson';

export type TimetableItems = Array<Array<LessonItem>>;

export type LessonItem = {
  length: number;
  color?: string;
  title: Slot;
  bottomLeft: Slot;
  bottomRight: Slot;
  lessonId: string;
} | null;

export const getTimetableMatrix = (slots: number, days: number) => {
  const result: TimetableItems = Array.from({ length: slots - 1 });
  for (let i = 0; i < slots; i++) {
    result[i] = Array.from<LessonItem>({ length: days }).fill(null);
  }
  return result;
};

export const mergeLessons = ((timetable: TimetableItems) => {
  const result: TimetableItems = [...timetable];
  for (let i = 0; i < result.length - 1; i++) {
    for (let j = 0; j < result[i].length; j++) {
      let k = i + 1;

      if (result[i][j] == null) {
        continue;
      }

      while (k < result.length && result[i][j]?.lessonId === result[k][j]?.lessonId) {
        result[i][j]!.length++;
        result[k][j] = null;
        k++;
      }
      i = k - 1;
    }
  }
  return result;
}) as (timetable: TimetableItems) => TimetableItems;

export const resourceDisplayName = {
  class: 'Klasse',
  room: 'Raum',
  student: 'Schüler',
  teacher: 'Lehrer',
};

export type Resource = keyof typeof resourceDisplayName;

export const dayOrder = {
  [Day.Monday]: 0,
  [Day.Tuesday]: 1,
  [Day.Wednesday]: 2,
  [Day.Thursday]: 3,
  [Day.Friday]: 4,
};

const initTimetable = async () => {
  const timeslots = await getTimeslots();
  const maxSlot = Math.max(...timeslots.map((timeslot) => timeslot.slot + 1), 0);

  return getTimetableMatrix(maxSlot, Object.keys(Day).length);
};

export const getStudentTimetableLessons = async (response: LessonResponseDto, selectedTimetableId: string) => {
  const result = await initTimetable();
  const lessons = response.lessons.filter((lesson) => lesson.timetableId === selectedTimetableId);

  for (const lesson of lessons) {
    const subject = response.subjects.find((subject) => subject.id === lesson.subjectId)!;
    const teacher = response.teachers.find((teacher) => teacher.id === lesson.teacherId)!;
    const room = response.rooms.find((room) => room.id === lesson.roomId)!;

    result[lesson.timeslot.slot][dayOrder[lesson.timeslot.day]] = {
      title: { text: subject.name, onClick: () => goto(`/admin/subjects/${subject.id}`) },
      bottomLeft: { text: teacher.acronym, onClick: () => goto(`/admin/teachers/${teacher.id}`) },
      bottomRight: {
        text: `${room.buildingName} - ${room.name}`,
        onClick: () => goto(`/admin/rooms/${room.id}`),
      },
      color: subject.color,
      lessonId: lesson.id,
      length: 1,
    };
  }

  return mergeLessons(result);
};

export const getClassTimetableLessons = async (response: LessonResponseDto, selectedTimetableId: string) => {
  const result = await initTimetable();
  const lessons = response.lessons.filter((lesson) => lesson.timetableId === selectedTimetableId);

  for (const lesson of lessons) {
    const subject = response.subjects.find((subject) => subject.id === lesson.subjectId)!;
    const room = response.rooms.find((room) => room.id === lesson.roomId)!;
    const teacher = response.teachers.find((teacher) => teacher.id === lesson.teacherId)!;

    result[lesson.timeslot.slot][dayOrder[lesson.timeslot.day]] = {
      title: { text: subject.name, onClick: () => goto(`/admin/subjects/${subject.id}`) },
      bottomLeft: { text: teacher.acronym, onClick: () => goto(`/admin/teachers/${teacher.id}`) },
      bottomRight: {
        text: `${room.buildingName} — ${room.name}`,
        onClick: () => goto(`/admin/rooms/${room.id}`),
      },
      color: subject.color,
      lessonId: lesson.id,
      length: 1,
    };
  }

  return mergeLessons(result);
};

export const getRoomTimetableLessons = async (response: LessonResponseDto, selectedTimetableId: string) => {
  const result = await initTimetable();
  const lessons = response.lessons.filter((lesson) => lesson.timetableId === selectedTimetableId);

  for (const lesson of lessons) {
    const subject = response.subjects.find((subject) => subject.id === lesson.subjectId)!;
    const teacher = response.teachers.find((teacher) => teacher.id === lesson.teacherId)!;
    const studentGroup = response.groups.find((group) => group.id === lesson.groupId)!;

    result[lesson.timeslot.slot][dayOrder[lesson.timeslot.day]] = {
      title: { text: subject.name, onClick: () => goto(`/admin/subjects/${subject.id}`) },
      bottomLeft: { text: teacher.acronym, onClick: () => goto(`/admin/teachers/${teacher.id}`) },
      bottomRight: { text: studentGroup.name, onClick: () => goto(`/admin/studentGroups/${studentGroup.id}`) },
      color: subject.color,
      lessonId: lesson.id,
      length: 1,
    };
  }

  return mergeLessons(result);
};

export const getTeacherTimetableLessons = async (response: LessonResponseDto, selectedTimetableId: string) => {
  const result = await initTimetable();
  const lessons = response.lessons.filter((lesson) => lesson.timetableId === selectedTimetableId);

  for (const lesson of lessons) {
    const subject = response.subjects.find((subject) => subject.id === lesson.subjectId)!;
    const room = response.rooms.find((room) => room.id === lesson.roomId)!;
    const studentGroup = response.groups.find((group) => group.id === lesson.groupId)!;

    result[lesson.timeslot.slot][dayOrder[lesson.timeslot.day]] = {
      title: { text: subject.name, onClick: () => goto(`/admin/subjects/${subject.id}`) },
      bottomLeft: { text: studentGroup.name, onClick: () => goto(`/admin/studentGroups/${studentGroup.id}`) },
      bottomRight: {
        text: `${room.buildingName} - ${room.name}`,
        onClick: () => goto(`/admin/rooms/${room.id}`),
      },
      color: subject.color,
      lessonId: lesson.id,
      length: 1,
    };
  }

  return mergeLessons(result);
};
