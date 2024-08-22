import {
  deleteConstraintInstance,
  deleteGrade,
  deleteRoom,
  deleteStudent,
  deleteSubject,
  deleteTag,
  deleteTeacher,
  getConstraintInstances,
  getGrades,
  getRooms,
  getStudentGroup,
  getStudents,
  getSubjects,
  getTags,
  getTeachers,
  removeStudentsFromStudentGroup,
  type GradeResponseDto,
  type Pageable,
  type PageStudent,
  type PageTeacher,
  type Sort,
} from '$lib/sdk/fetch-client';
import { error } from '@sveltejs/kit';
import { toast as _toast } from 'svelte-sonner';

export type DataItem = {
  id: string;

  [key: string]: string | string[] | number;
};

async function deleteMultiple(ids: string[], deleteEntry: (id: string) => Promise<void>) {
  return Promise.all(ids.map((id) => deleteEntry(id)));
}

export const toast = (success: boolean, message: string) =>
  success ? _toast.success(message) : _toast.error(message, { important: true, duration: 4000 });

export async function loadStudentPage(sortString: string, filter: string, index?: number, pageSize?: number) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  const result: PageStudent = await getStudents(pageable, {
    search: filter,
  });
  const dataItems: DataItem[] = result.content
    ? result.content.map(
        (student): DataItem => ({
          id: student.id,
          firstName: student.firstName,
          lastName: student.lastName,
          tags: student.tags.map(({ name }) => name),
        }),
      )
    : [];
  return {
    data: dataItems,
    totalElements: Number(result.totalElements),
  };
}

export async function deleteStudentEntry(ids: string[]) {
  try {
    await deleteMultiple(ids, deleteStudent);
  } catch {
    toast(false, 'Beim Löschen des Schülers ist ein Fehler aufgetreten');
    error(400, { message: `could not delete students` });
  }
}

export async function loadRoomPage(sortString: string, filter: string, index?: number, pageSize?: number) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  const result = await getRooms(pageable, {
    search: filter,
  });
  const dataItems: DataItem[] = result.content
    ? result.content.map(
        (room): DataItem => ({
          id: room.id,
          name: room.name,
          buildingName: room.buildingName,
          capacity: room.capacity,
          tags: room.tags.map(({ name }) => name),
        }),
      )
    : [];
  return {
    data: dataItems,
    totalElements: Number(result.totalElements),
  };
}

export async function deleteRoomEntry(ids: string[]) {
  try {
    await deleteMultiple(ids, deleteRoom);
  } catch {
    toast(false, 'Beim Löschen des Raums ist ein Fehler aufgetreten');
    error(400, { message: `could not delete rooms` });
  }
}

export async function loadSubjects(sortString: string, filter: string) {
  const sort: Sort = { sort: [sortString] };
  const result = await getSubjects(sort, {
    search: filter,
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
}

export async function deleteSubjectEntry(ids: string[]) {
  try {
    await deleteMultiple(ids, deleteSubject);
  } catch {
    toast(false, 'Beim Löschen des Fachs ist ein Fehler aufgetreten');
    error(400, { message: `could not delete subjects` });
  }
}

export async function loadTags(sortString: string, filter: string) {
  const sort: Sort = { sort: [sortString] };

  const result = await getTags(sort, {
    search: filter,
  });
  const dataItems: DataItem[] = result.map((tag) => ({
    id: tag.id,
    name: tag.name,
  }));

  return {
    data: dataItems,
    totalElements: result.length,
  };
}

export async function deleteTagEntry(ids: string[]) {
  try {
    await deleteMultiple(ids, deleteTag);
  } catch {
    toast(false, 'Beim Löschen des Fachs ist ein Fehler aufgetreten');
    error(400, { message: `could not delete tags` });
  }
}

export async function loadTeacherPage(sortString: string, filter: string, index?: number, pageSize?: number) {
  const pageable: Pageable = { page: index, size: pageSize, sort: [sortString] };
  const result: PageTeacher = await getTeachers(pageable, {
    search: filter,
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
}

export async function deleteTeacherEntry(ids: string[]) {
  try {
    await deleteMultiple(ids, deleteTeacher);
  } catch {
    toast(false, 'Beim Löschen des Lehrers ist ein Fehler aufgetreten');
    error(400, { message: `could not delete teachers` });
  }
}

export async function loadGrades(sortString: string, filter: string) {
  const sort: Sort = { sort: [sortString] };
  const result: GradeResponseDto[] = await getGrades(sort, {
    search: filter,
  });
  const dataItems: DataItem[] = result.map((grade) => ({
    id: grade.id,
    name: grade.name,
    tags: grade.tags.map((tag) => tag.name),
  }));
  return {
    data: dataItems,
    totalElements: dataItems.length,
  };
}

export async function deleteGradeEntry(ids: string[]) {
  try {
    await deleteMultiple(ids, deleteGrade);
  } catch {
    toast(false, 'Beim Löschen der Stufe ist ein Fehler aufgetreten');
    error(400, { message: `could not delete grades` });
  }
}

export async function getInstancesPage(
  toSort: string,
  filter: string,
  pageIndex?: number,
  pageSize?: number,
  constraintSignatureId?: string,
) {
  const pageable: Pageable = { page: pageIndex, size: pageSize, sort: [toSort] };
  if (!constraintSignatureId) {
    throw error(400, { message: 'Could not fetch page' });
  }
  const result = await getConstraintInstances(constraintSignatureId, pageable, { argument: filter });
  const dataItems: DataItem[] = result.constraintInstances.map((instance) => {
    const item: DataItem = { id: instance.id };
    for (let i = 0; i < instance.arguments.length; i++) {
      item[`name${i}`] = result.displayNames.find((item) => item.id === instance.arguments[i].value)?.displayName ?? '';
    }
    return item;
  });
  return {
    data: dataItems,
    totalElements: result.totalElements,
  };
}

export async function deleteInstances(ids: string[], signatureId?: string) {
  try {
    await deleteConstraintInstance(signatureId || '', ids);
  } catch {
    toast(false, 'Beim Löschen der Constraint Instanz ist ein Fehler aufgetreten');
    error(400, { message: `could not delete constraint instances` });
  }
}

export async function getStudentsFromGroup(
  toSort: string,
  filter: string,
  index?: number,
  pageSize?: number,
  additionalId?: string,
) {
  if (!additionalId) {
    throw error(400, { message: 'Invalid student group id' });
  }
  const { students } = await getStudentGroup(additionalId);
  const dataItems: DataItem[] = students.map(
    (student): DataItem => ({
      id: student.id,
      firstName: student.firstName,
      lastName: student.lastName,
      tags: student.tags.map(({ name }) => name),
    }),
  );
  return {
    data: dataItems,
    totalElements: Number(students.length),
  };
}

export async function removeStudentFromGroup(studentIds: string[], studentGroupId?: string) {
  if (!studentGroupId) {
    throw error(400, { message: `Student group id ${studentGroupId} is invalid` });
  }
  try {
    await removeStudentsFromStudentGroup(studentGroupId, studentIds);
  } catch {
    toast(false, 'Beim Entfernen des Schülers aus einer Gruppe ist ein Fehler aufgetreten');
    error(400, { message: 'Could not remove students from group' });
  }
}
