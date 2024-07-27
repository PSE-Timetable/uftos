import {
  deleteRoom,
  deleteStudent,
  deleteSubject,
  deleteTag,
  deleteTeacher,
  getRooms,
  getStudents,
  getSubjects,
  getTags,
  getTeachers,
  type Pageable,
  type PageRoom,
  type PageStudent,
  type PageTeacher,
  type Sort,
} from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';

export type DataItem = {
  id: string;

  [key: string]: string | string[] | number;
};

const pageSize = 15;

export async function loadStudentPage(index: number, sortString: string, filter: string) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  try {
    const result: PageStudent = await getStudents(pageable, {
      firstName: filter,
      lastName: filter,
    });
    const dataItems: DataItem[] = result.content
      ? result.content.map(
          (student): DataItem => ({
            id: student.id,
            firstName: student.firstName,
            lastName: student.lastName,
            tags: student.tags.map((tag) => tag.name),
          }),
        )
      : [];
    return {
      data: dataItems,
      totalElements: Number(result.totalElements),
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}

export async function deleteStudentEntry(id: string) {
  try {
    await deleteStudent(id);
  } catch {
    error(400, { message: `could not delete student with id ${id}` });
  }
}

export async function loadRoomPage(index: number, sortString: string, filter: string) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  let result: PageRoom;
  try {
    result = await getRooms(pageable, {
      name: filter,
      buildingName: filter,
    });
    const dataItems: DataItem[] = result.content
      ? result.content.map(
          (room): DataItem => ({
            id: room.id,
            name: room.name,
            buildingName: room.buildingName,
            capacity: room.capacity,
            tags: room.tags.map((tag) => tag.name),
          }),
        )
      : [];
    return {
      data: dataItems,
      totalElements: Number(result.totalElements),
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}

export async function deleteRoomEntry(id: string) {
  try {
    await deleteRoom(id);
  } catch {
    error(400, { message: `could not delete room with id ${id}` });
  }
}

export async function loadSubjects(index: number, sortString: string, filter: string) {
  const sort: Sort = { sort: [sortString] };
  try {
    const result = await getSubjects(sort, {
      name: filter,
    });
    const dataItems: DataItem[] = result.map(
      (subject): DataItem => ({
        id: subject.id,
        name: subject.name,
        tags: subject.tags.map((tag) => tag.name),
      }),
    );

    return {
      data: dataItems,
      totalElements: result.length,
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}

export async function deleteSubjectEntry(id: string) {
  try {
    await deleteSubject(id);
  } catch {
    error(400, { message: `could not delete subject with id ${id}` });
  }
}

export async function loadTags(index: number, sortString: string, filter: string) {
  const sort: Sort = { sort: [sortString] };
  try {
    const result = await getTags(sort, {
      name: filter,
    });
    const dataItems: DataItem[] = result.map((tag) => ({
      id: tag.id,
      name: tag.name,
    }));

    return {
      data: dataItems,
      totalElements: result.length,
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}

export async function deleteTagEntry(id: string) {
  try {
    await deleteTag(id);
  } catch {
    error(400, { message: `could not delete tag with id ${id}` });
  }
}

export async function loadTeacherPage(index: number, sortString: string, filter: string) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  try {
    const result: PageTeacher = await getTeachers(pageable, {
      firstName: filter,
      lastName: filter,
      acronym: filter,
    });
    const dataItems: DataItem[] = result.content
      ? result.content.map(
          (teacher): DataItem => ({
            id: teacher.id,
            firstName: teacher.firstName,
            lastName: teacher.lastName,
            acronym: teacher.acronym,
            subjects: teacher.subjects.map((subject) => subject.name),
            tags: teacher.tags.map((tag) => tag.name),
          }),
        )
      : [];
    return {
      data: dataItems,
      totalElements: Number(result.totalElements),
    };
  } catch {
    error(400, { message: 'Could not fetch page' });
  }
}

export async function deleteTeacherEntry(id: string) {
  try {
    await deleteTeacher(id);
  } catch {
    error(400, { message: `could not delete teacher with id ${id}` });
  }
}
