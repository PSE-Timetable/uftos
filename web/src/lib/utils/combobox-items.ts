import type { ComboBoxItem } from '$lib/elements/ui/combo-box/combo-box';
import {
  getGrades,
  getRooms,
  getStudentGroups,
  getStudents,
  getSubjects,
  getTags,
  getTeachers,
  getTimeslots,
} from '$lib/sdk/fetch-client';
import type { Args } from '@oazapfts/runtime';

type FunctionType<T> = (...args: Args<T>) => Promise<ComboBoxItem[]>;

export const getGradesItems: FunctionType<typeof getGrades> = async (sort, opts) => {
  const grades = await getGrades(sort, opts);
  return grades.map((grade) => ({ value: grade.id, label: grade.name }));
};

export const getSubjectsItems: FunctionType<typeof getSubjects> = async (sort, opts) => {
  const subjects = await getSubjects(sort, opts);
  return subjects.map((subject) => ({ value: subject.id, label: subject.name }));
};

export const getRoomsItems: FunctionType<typeof getRooms> = async (page, opts) => {
  const { content } = await getRooms(page, opts);
  return (content || []).map((room) => ({
    value: room.id,
    label: `${room.buildingName}: ${room.name}`,
  }));
};

export const getStudentGroupsItems: FunctionType<typeof getStudentGroups> = async (page, opts) => {
  const { content } = await getStudentGroups(page, opts);
  return (content || []).map((studentGroup) => ({ value: studentGroup.id, label: studentGroup.name }));
};

export const getStudentsItems: FunctionType<typeof getStudents> = async (page, opts) => {
  const { content } = await getStudents(page, opts);
  return (content || []).map((student) => ({
    value: student.id,
    label: `${student.firstName} ${student.lastName}`,
  }));
};

export const getTagsItems: FunctionType<typeof getTags> = async (sort, opts) => {
  const tags = await getTags(sort, opts);
  return tags.map((tag) => ({ value: tag.id, label: tag.name }));
};

export const getTeachersItems: FunctionType<typeof getTeachers> = async (page, opts) => {
  const { content } = await getTeachers(page, opts);
  return (content || []).map((teacher) => ({
    value: teacher.id,
    label: `${teacher.firstName} ${teacher.lastName}`,
  }));
};

export const getTimeslotsItems: FunctionType<typeof getTimeslots> = async (params, opts) => {
  const timeslots = await getTimeslots(params, opts);
  return timeslots.map((timeslot) => ({
    value: timeslot.id,
    label: `${timeslot.day}: ${timeslot.slot}`,
  }));
};
